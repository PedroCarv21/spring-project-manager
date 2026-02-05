package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.TarefaResponseDTO;
import com.example.springprojectmanager.dtos.TarefaUsuariosResponseDTO;
import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.enums.StatusTarefaAtualizacao;
import com.example.springprojectmanager.mappers.TarefaMapper;
import com.example.springprojectmanager.services.TarefaService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;
    private final TarefaMapper tarefaMapper;

    @PostMapping("/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacaoParaSolicitar(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
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
    @PreAuthorize("@projetoService.possuiAutorizacaoParaSolicitar(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
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
    @PreAuthorize("@tarefaService.temPermissaoParaInteragirComTarefa(#id, #nomeTarefa, @fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado().getNome()) " +
            "and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<TarefaUsuariosResponseDTO> buscarTarefa(
            @PathVariable("id")
            UUID id,
            @RequestParam("nome_tarefa")
            String nomeTarefa){
        Tarefa tarefa = this.tarefaService.buscarTarefa(id, nomeTarefa);
        TarefaUsuariosResponseDTO tarefaUsuariosResponseDTO = this.tarefaMapper.toTarefaUsuariosResponseDTO(tarefa);
        return ResponseEntity.ok(tarefaUsuariosResponseDTO);
    }

    @DeleteMapping("desvicular_tarefa_usuario/{id}")
    @PreAuthorize("@projetoService.possuiAutorizacaoParaSolicitar(#id) " +
            "and @tarefaService.temPermissaoParaInteragirComTarefa(#id, #nomeTarefa, @fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado().getNome()) " +
            "and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
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
    @PreAuthorize("@projetoService.possuiAutorizacaoParaSolicitar(#id) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<Void> deletar(
            @PathVariable("id")
            UUID id,
            @RequestParam("nome_tarefa")
            String nomeTarefa){

        this.tarefaService.deletar(id, nomeTarefa);
        return ResponseEntity.noContent().build();
    }
}
