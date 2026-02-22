package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.ProjetoTimeReponseDTO;
import com.example.springprojectmanager.dtos.TimeEUsuariosResponseDTO;
import com.example.springprojectmanager.dtos.TimeResponseDTO;
import com.example.springprojectmanager.dtos.UsuarioRoleResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.RoleParticipante;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.mappers.TimeMapper;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.ProjetoService;
import com.example.springprojectmanager.services.TimeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
@Validated
public class TimeController implements CriadorLocation{

    private final TimeService timeService;
    private final TimeMapper timeMapper;
    private final ProjetoService projetoService;
    private final ProjetoMapper projetoMapper;
    private final ProjetoRepository projetoRepository;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
    @Operation(
            description = """
                    Consulta os times daquele projeto. Se não for informado o nome do time, a resposta será os dados do projeto e a lista de todos times
                    vinculados a ele. No entanto, informe o nome do time, será retornado os dados do projeto e somente o time solicitado. Se não for encontrado
                    irá retornar a mensagem "Não existe um time chamado" + nome do time informado.
                    """)
    public ResponseEntity<List<ProjetoTimeReponseDTO>> pesquisar(
            @NotBlank(message = "Informe o nome do projeto")
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time", required = false)
            String nomeTime){

        List<Projeto> projetos = this.timeService.pesquisar(nomeProjeto, nomeTime);
        List<ProjetoTimeReponseDTO> projetoTimeReponseDTOList = projetos.stream().map(this.projetoMapper::toProjetoTimeResponseDTO).toList();
        return ResponseEntity.ok(projetoTimeReponseDTOList);
    }

    @PostMapping
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Cria um novo time vinculado ao projeto informado. Não é permitido
                    criar dois times com o mesmo nome.
                    """)
    public ResponseEntity<ProjetoTimeReponseDTO> salvar(
            @NotBlank(message = "Informe um nome para o novo projeto.")
            @RequestParam("nome_projeto") String nomeProjeto,
            @NotBlank(message = "Informe um nome para o novo time.")
            @RequestParam("nome_time") String nomeTime
    ){
        Time timeSalvo = this.timeService.salvar(nomeProjeto, nomeTime);

        ProjetoTimeReponseDTO projetoTimeResponseDTO = this.projetoMapper.toProjetoTimeResponseDTO(timeSalvo.getProjeto());
        return ResponseEntity.created(gerarLocation(timeSalvo.getId())).body(projetoTimeResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza somente o nome do time.
                    """)
    public ResponseEntity<TimeResponseDTO> atualizar(
            @NotBlank(message = "Informe o nome do projeto.")
            @RequestParam("nome_projeto") String nomeProjeto,
            @NotBlank(message = "Informe o nome atual do time.")
            @RequestParam("nome_atual_time") String nomeAtualTime,
            @RequestParam(value = "novo_nome_time") String novoNomeTime){

        Time timeAtualizado = this.timeService.atualizar(nomeProjeto, nomeAtualTime, novoNomeTime);
        TimeResponseDTO timeResponseDTO = this.timeMapper.toDTO(timeAtualizado);
        return ResponseEntity.ok(timeResponseDTO);
    }

    @PutMapping("atualizar_participante")
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza a role do usuário do projeto para 'MANAGER' ou 'MEMBER'.
                    Não é possível atualizar a role para ADMIN, pois só aquele que cria
                    o projeto pode ser o administrador.
                    """)
    public ResponseEntity<TimeEUsuariosResponseDTO> atualizarParticipante(
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_usuario")
            String username,
            @RequestParam(name = "role")
            RoleParticipante roleParticipante){
        Role role = this.usuarioMapper.toRole(roleParticipante);
        Time time = this.timeService.atualizarRoleParticipante(nomeProjeto, username, role);
        TimeEUsuariosResponseDTO timeEUsuariosResponseDTO = this.timeMapper.toTimeEUsuariosResponseDTO(time);

        List<UsuarioRoleResponseDTO> usuarioRoleResponseDTOList = timeEUsuariosResponseDTO
                .usuarioRoleResponseDTOList()
                .stream()
                .filter(usuarioRoleResponseDTO -> usuarioRoleResponseDTO.nome().equals(username))
                .toList();

        TimeEUsuariosResponseDTO timeEUsuariosResponseDTOAtualizado = new TimeEUsuariosResponseDTO(
                timeEUsuariosResponseDTO.nome(),
                timeEUsuariosResponseDTO.status(),
                timeEUsuariosResponseDTO.dataCriacao(),
                timeEUsuariosResponseDTO.dataAtualizacao(),
                timeEUsuariosResponseDTO.nomeProjeto(),
                usuarioRoleResponseDTOList
        );
        return ResponseEntity.ok(timeEUsuariosResponseDTOAtualizado);
    }

    @PutMapping("/adicionar_participante")
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Adiciona um participante no projeto, vinculando ele a
                    um time já desde o início.
                    """)
    public ResponseEntity<TimeEUsuariosResponseDTO> adicionarParticipante(
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time")
            String nomeTime,
            @RequestParam(name = "nome_usuario")
            String username,
            @RequestParam(name = "role")
            RoleParticipante roleParticipante){

        Role role = this.usuarioMapper.toRole(roleParticipante);
        Time time = this.timeService.adicionarParticipante(nomeProjeto, nomeTime, username, role);
        TimeEUsuariosResponseDTO timeEUsuariosResponseDTO = this.timeMapper.toTimeEUsuariosResponseDTO(time);
        return ResponseEntity.ok(timeEUsuariosResponseDTO);
    }


    @PutMapping("/reativar")
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Muda o status do time informado para ATIVO.
                    """)
    public ResponseEntity<TimeResponseDTO> reativar(
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time")
            String nomeTime){
        Time time = this.timeService.reativar(nomeProjeto, nomeTime);
        TimeResponseDTO timeResponseDTO = this.timeMapper.toDTO(time);
        return ResponseEntity.ok(timeResponseDTO);
    }

    @DeleteMapping
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Muda o status do time informado para ENCERRADO. Se tentar encerrar o mesmo time
                    mais de uma vezes, aparecerá a mensagem "Não é possível realizar essa ação 
                    pois o time " + nome do time +" foi encerrado.".
                    """)
    public ResponseEntity<Void> deletar(
            @NotBlank(message = "Informe o nome de um projeto")
            @RequestParam("nome_projeto")
            String nomeProjeto,
            @NotBlank(message = "Informe o nome de um time")
            @RequestParam("nome_time")
            String nomeTime){
        this.timeService.deletar(nomeProjeto, nomeTime);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/excluir_participante")
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Exclui o usuário informado do projeto, considerando que um usuário (MEMBER ou MANAGER) só está
                    vinculado a um projeto se ele estiver vinculado a algum time do projeto.
                    """)
    public ResponseEntity<Void> excluirUsuario(
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time")
            String nomeTime,
            @RequestParam(name = "nome_usuario")
            String username){

        this.timeService.excluirUsuario(nomeProjeto, nomeTime, username);
        return ResponseEntity.noContent().build();
    }

}
