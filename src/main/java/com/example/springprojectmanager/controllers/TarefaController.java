package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.TarefaResponseDTO;
import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.mappers.TarefaMapper;
import com.example.springprojectmanager.services.TarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;
    private final TarefaMapper tarefaMapper;

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> salvar(
            @RequestParam(name = "id")
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
}
