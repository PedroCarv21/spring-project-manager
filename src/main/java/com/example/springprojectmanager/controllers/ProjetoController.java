package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.services.ProjetoService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("projetos/")
@RequiredArgsConstructor
@Validated
public class ProjetoController implements CriadorLocation{

    private final ProjetoService projetoService;
    private final ProjetoMapper projetoMapper;

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> salvar(
            @RequestParam(value = "nome")
            @NotBlank(message = "Informe algum nome para o seu projeto.")
            String nome){
        Projeto projeto = new Projeto(nome, StatusProjeto.INICIADO);
        Projeto projetoSalvo = this.projetoService.salvar(projeto);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoSalvo);
        return ResponseEntity.created(gerarLocation(projetoSalvo.getId())).body(projetoResponseDTO);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @PathVariable("id")
            UUID id,
            @NotBlank(message = "Informe algum nome para o seu projeto.")
            @RequestParam(value = "nome")
            String nome,
            @RequestParam(value = "status")
            StatusProjetoAtualizacao statusProjetoAtualizacao){

        StatusProjeto statusProjeto = this.projetoMapper.toStatusProjeto(statusProjetoAtualizacao);

        Projeto projeto = new Projeto(nome, statusProjeto);
        projeto.setId(id);
        Projeto projetoSalvo = this.projetoService.atualizar(projeto);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoSalvo);
        return ResponseEntity.ok(projetoResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProjetoResponseDTO>> pesquisar(
            @RequestParam(name = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(name = "tamanho_pagina", defaultValue = "3") Integer tamanhoPagina,
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "status", required = false) StatusProjeto statusProjeto
    ){
        Page<Projeto> pesquisa = this.projetoService.pesquisar(pagina, tamanhoPagina, nome, statusProjeto);

        Page<ProjetoResponseDTO> projetoResponseDTOPage = pesquisa.map(this.projetoMapper::toDTO);
        return ResponseEntity.ok(projetoResponseDTOPage);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(
            @NotBlank(message = "Informe o nome de um projeto")
            @RequestParam("nome") String nome){
        this.projetoService.deletar(nome);
        return ResponseEntity.noContent().build();
    }
}
