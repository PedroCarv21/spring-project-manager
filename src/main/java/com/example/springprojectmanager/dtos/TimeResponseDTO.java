package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.StatusTime;

import java.time.LocalDateTime;

public record TimeResponseDTO(
        String nome,
        StatusTime status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        String nomeProjeto) {
}
