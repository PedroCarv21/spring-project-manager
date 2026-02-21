package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.TarefaResponseDTO;
import com.example.springprojectmanager.dtos.TarefaUsuariosResponseDTO;
import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.enums.StatusTarefaAtualizacao;
import com.example.springprojectmanager.mappers.TarefaMapper;
import com.example.springprojectmanager.services.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;
    private final TarefaMapper tarefaMapper;

    @PostMapping("/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacao(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Cria uma nova tarefa, que pode ou não estar inicialmente vinculada a um time específico. No entanto,
                    se for gerente, é necessário informar o nome do seu time. Não é possível criar duas ou mais tarefas com
                    o mesmo nome.
                    """)
    public ResponseEntity<TarefaResponseDTO> salvar(
            @PathVariable("id")
            UUID id,
            @RequestParam(name = "nome_time", required = false)
            String nomeTime,
            @RequestParam(name = "nome_tarefa")
            String nomeTarefa,
            @RequestParam(name = "descricao")
            String descricao){
        Tarefa tarefa = this.tarefaService.salvar(id, nomeTime, nomeTarefa, descricao);
        TarefaResponseDTO tarefaResponseDTO = this.tarefaMapper.toDTO(tarefa);
        return ResponseEntity.ok(tarefaResponseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@tarefaService.temPermissaoParaInteragirComTarefa(#id, #antigoNomeTarefa, @fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado().getNome()) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    A atualização da tarefa é possível tanto para todos os tipos de usuários vinculados aquele projeto. Deve-se seguir as seguintes regras: \n
                    - A tarefa não pode estar com status 'CANCELADO'. \n
                    - Caso seja um usuário MANAGER, só poderá atualizar tarefas do seu time. \n
                    - Caso seja um usuário MEMBER, só poderá atualizar tarefas vinculadas a você. \n
                    - Caso seja um usuário MEMBER, só poderá atualizar o status da tarefa.
                    """)
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable("id")
            UUID id,
            @RequestParam(name = "nome_time", required = false)
            String nomeTime,
            @RequestParam(name = "antigo_nome_tarefa")
            String antigoNomeTarefa,
            @RequestParam(name = "novo_nome_tarefa", required = false)
            String novoNomeTarefa,
            @RequestParam(name = "descricao", required = false)
            String descricao,
            @RequestParam(name = "status_tarefa", required = false)
            StatusTarefaAtualizacao statusTarefaAtualizacao
    ){
        StatusTarefa statusTarefa = this.tarefaMapper.toStatusTarefa(statusTarefaAtualizacao);
        Tarefa tarefa = this.tarefaService.atualizar(id, nomeTime, antigoNomeTarefa, novoNomeTarefa, descricao, statusTarefa);

        TarefaResponseDTO tarefaResponseDTO = this.tarefaMapper.toDTO(tarefa);
        return ResponseEntity.ok(tarefaResponseDTO);
    }

    @PutMapping("vincular_tarefa_usuario/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacao(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Vincula um usuário (que não seja o próprio administrador) a uma tarefa, sendo necessário que ambos
                    estejam no mesmo time. Caso você seja um gerente, é necessário estar no mesmo time que tarefa e o usuário.
                    Não será possível criar um vínculo caso a tarefa esteja com o status CANCELADO.
                    """)
    public ResponseEntity<TarefaUsuariosResponseDTO> vincularTarefaAUmParticipante(
            @PathVariable("id")
            UUID id,
            @RequestParam(name = "nome_tarefa")
            String nomeTarefa,
            @RequestParam(name = "username")
            String username){
        Tarefa tarefa = this.tarefaService.vincularTarefaAUmParticipante(id, nomeTarefa, username);
        TarefaUsuariosResponseDTO tarefaUsuariosResponseDTO = this.tarefaMapper.toTarefaUsuariosResponseDTO(tarefa);
        return ResponseEntity.ok(tarefaUsuariosResponseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@projetoUsuarioService.existeProjetoUsuario(@projetoService.capturarProjetoPorId(#id), @fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado()) " +
            "and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Há quatro possibilidades de consulta: \n
                    - Se for o administrador e informar apenas o id do projeto, serão expostas todas as tarefas do projeto. \n
                    - Se for o administrador e informar também o nome da tarefa, será apresentado aquela tarefa em especial. \n
                    - Se for um usuário (MEMBER ou MANAGER) e informar apenas o id do projeto, serão apresentados todos
                    as tarefas do seu devido time. \n
                    - Se for um usuário (MEMBER ou MANAGER) e informar também o nome da tarefa, que deve estar vinculada ao 
                    time do usuário, será apresentado aquela tarefa em especial. Se tentar solicitar uma tarefa que não está
                    no seu time, aparecerá a mensagem "Esta tarefa não se encontra no seu time."
                    """)
    public ResponseEntity<List<TarefaUsuariosResponseDTO>> buscarTarefas(
            @PathVariable("id")
            UUID id,
            @RequestParam(value = "nome_tarefa", required = false)
            String nomeTarefa){

        List<Tarefa> tarefas = this.tarefaService.buscarTarefas(id, nomeTarefa);
        List<TarefaUsuariosResponseDTO> tarefaUsuariosResponseDTOList = tarefas
                .stream()
                .map(this.tarefaMapper::toTarefaUsuariosResponseDTO)
                .toList();
        return ResponseEntity.ok(tarefaUsuariosResponseDTOList);
    }

    @DeleteMapping("desvicular_tarefa_usuario/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacao(#id) " +
            "and @tarefaService.temPermissaoParaInteragirComTarefa(#id, #nomeTarefa, @fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado().getNome()) " +
            "and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Desvincula tarefa do usuário. Caso eles já não estejam vinculados, aparecerá
                    uma mensagem "Usuário e tarefa não estão vinculados".
                    """)
    public ResponseEntity<TarefaUsuariosResponseDTO> desvincularTarefaDoUsuario(
            @PathVariable("id")
            UUID id,
            @RequestParam("nome_tarefa")
            String nomeTarefa,
            @RequestParam("nome_usuario")
            String username){
        Tarefa tarefa = this.tarefaService.desvincularTarefaDoUsuario(id, nomeTarefa, username);
        TarefaUsuariosResponseDTO tarefaUsuariosResponseDTO = this.tarefaMapper.toTarefaUsuariosResponseDTO(tarefa);
        return ResponseEntity.ok(tarefaUsuariosResponseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacao(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Muda o status da tarefa para CANCELADO. Se a tarefa já estiver cancelada, aparecerá a mensagem
                    "A tarefa já está cancelada.".
                    """)
    public ResponseEntity<Void> deletar(
            @PathVariable("id")
            UUID id,
            @RequestParam("nome_tarefa")
            String nomeTarefa){

        this.tarefaService.deletar(id, nomeTarefa);
        return ResponseEntity.noContent().build();
    }
}
