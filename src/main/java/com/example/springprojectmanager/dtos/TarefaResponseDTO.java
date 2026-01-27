package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.StatusTarefa;

import java.time.LocalDateTime;

public record TarefaResponseDTO(
        String nome,
        String descricao,
        StatusTarefa status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        String nomeProjeto,
        String nomeTime) {
}
