package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("projetos/")
@RequiredArgsConstructor
@Validated
public class ProjetoController implements CriadorLocation{

    private final ProjetoService projetoService;
    private final ProjetoMapper projetoMapper;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

    @PostMapping
    @PreAuthorize("@fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Cria um projeto novo para o usuário, sendo necessário apenas informar o nome do novo projeto. 
                    Dois ou mais projetos não podem ter o mesmo nome, caso pertençam ao mesmo usuário. Se fizer isso, ocorrerá um erro, aparecendo a 
                    seguinte mensagem: "Voce ja criou um projeto chamado " + nome do projeto. Quando você cria um projeto, você se torna o seu único administrador.
                    """)
    public ResponseEntity<ProjetoResponseDTO> salvar(
            @RequestParam(value = "nome")
            @NotBlank(message = "Informe algum nome para o seu projeto.")
            String nome){
        Projeto projetoSalvo = this.projetoService.salvar(nome);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoSalvo);
        return ResponseEntity.created(gerarLocation(projetoSalvo.getId())).body(projetoResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@projetoService.possuiAutorizacao(#nomeAtual) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza o nome e o status do projeto. Você deve informar o nome atual do projeto e, em seguida, o novo nome e status que você deseja. 
                    O novo nome não pode ser referente ao nome de um projeto que já existe e é administrado pelo usuário.
                    """)
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @NotBlank(message = "Informe nome atual do seu projeto.")
            @RequestParam(value = "nome_atual")
            String nomeAtual,
            @RequestParam(value = "novo_nome", required = false)
            String novoNome,
            @RequestParam(value = "status", required = false)
            StatusProjetoAtualizacao statusProjetoAtualizacao){

        StatusProjeto statusProjeto = this.projetoMapper.toStatusProjeto(statusProjetoAtualizacao);
        Projeto projetoAtualizado = this.projetoService.atualizar(nomeAtual, novoNome, statusProjeto);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoAtualizado);
        return ResponseEntity.ok(projetoResponseDTO);
    }

    @GetMapping
    @Operation(
            description = """
                    Realiza uma busca dos projetos do usuário autenticado com base no nome e/ou no status do projeto. 
                    Se esses dois campos não forem definidos, então será mostrado todos os projetos. Caso não seja encontrado nenhum projeto, 
                    será exposto a seguinte mensagem: Nenhum projeto foi encontrado.
                    """)
    public ResponseEntity<List<ProjetoResponseDTO>> pesquisar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "status", required = false) StatusProjeto statusProjeto
    ){

        List<Projeto> projetos = this.projetoService.pesquisar(nome, statusProjeto);

        List<ProjetoResponseDTO> projetoResponseDTOList = projetos.stream().map(this.projetoMapper::toDTO).toList();
        return ResponseEntity.ok(projetoResponseDTOList);
    }

    @DeleteMapping
    @PreAuthorize("@projetoService.possuiAutorizacao(#nome) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza o status do projeto para CANCELADO. Ao fazer isso, o administrador será impossibilitado de: \n
                    - Criar novos times ou atualizar/deletar times já existentes. \n
                    - Adicionar novos usuários ao time ou atualizar a role do membro. \n
                    - Criar novas tarefas ou atualizar/deletar tarefas já existentes. \n
                    - Atualizar o vínculo entre tarefa e usuário.
                    """)
    public ResponseEntity<Void> deletar(
            @NotBlank(message = "Informe o nome de um projeto")
            @RequestParam("nome") String nome){
        this.projetoService.deletar(nome);
        return ResponseEntity.noContent().build();
    }
}
