package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.services.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("projetos/")
@RequiredArgsConstructor
public class ProjetoController implements CriadorLocation{

    private final ProjetoService projetoService;
    private final ProjetoMapper projetoMapper;

    @PostMapping
    public ResponseEntity<Projeto> salvar(@RequestParam(value = "nome", required = true) String nome){
        Projeto projeto = new Projeto(nome, StatusProjeto.INICIADO);
        Projeto projetoSalvo = this.projetoService.salvar(projeto);
        return ResponseEntity.created(gerarLocation(projetoSalvo.getId())).body(projeto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizar(
            @PathVariable("id") UUID id,
            @RequestParam(value = "nome") String nome,
            @RequestParam(value = "status") StatusProjetoAtualizacao statusProjetoAtualizacao){

        StatusProjeto statusProjeto = this.projetoMapper.toStatusProjeto(statusProjetoAtualizacao);

        Projeto projeto = new Projeto(nome, statusProjeto);
        projeto.setId(id);
        Projeto projetoSalvo = this.projetoService.atualizar(projeto);
        return ResponseEntity.ok(projetoSalvo);
    }

    @GetMapping
    public ResponseEntity<Page<Projeto>> pesquisar(
            @RequestParam(name = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(name = "tamanho_pagina", defaultValue = "3") Integer tamanhoPagina,
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "status", required = false) StatusProjeto statusProjeto
    ){
        Page<Projeto> pesquisa = this.projetoService.pesquisar(pagina, tamanhoPagina, nome, statusProjeto);
        return ResponseEntity.ok(pesquisa);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(UUID id){
        this.projetoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
