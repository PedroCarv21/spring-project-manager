package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.ProjetoService;
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
    public ResponseEntity<ProjetoResponseDTO> salvar(
            @RequestParam(value = "nome")
            @NotBlank(message = "Informe algum nome para o seu projeto.")
            String nome){
        Projeto projetoSalvo = this.projetoService.salvar(nome);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoSalvo);
        return ResponseEntity.created(gerarLocation(projetoSalvo.getId())).body(projetoResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeAtual) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @NotBlank(message = "Informe nome atual do seu projeto.")
            @RequestParam(value = "nome_atual")
            String nomeAtual,
            @RequestParam(value = "novo_nome", required = false)
            String novoNome,
            @RequestParam(value = "status")
            StatusProjetoAtualizacao statusProjetoAtualizacao){

        StatusProjeto statusProjeto = this.projetoMapper.toStatusProjeto(statusProjetoAtualizacao);
        Projeto projetoAtualizado = this.projetoService.atualizar(nomeAtual, novoNome, statusProjeto);
        ProjetoResponseDTO projetoResponseDTO = this.projetoMapper.toDTO(projetoAtualizado);
        return ResponseEntity.ok(projetoResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> pesquisar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "status", required = false) StatusProjeto statusProjeto
    ){

        List<Projeto> projetos = this.projetoService.pesquisar(nome, statusProjeto);

        List<ProjetoResponseDTO> projetoResponseDTOList = projetos.stream().map(this.projetoMapper::toDTO).toList();
        return ResponseEntity.ok(projetoResponseDTOList);
    }

    @DeleteMapping
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nome) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<Void> deletar(
            @NotBlank(message = "Informe o nome de um projeto")
            @RequestParam("nome") String nome){
        this.projetoService.deletar(nome);
        return ResponseEntity.noContent().build();
    }
}
