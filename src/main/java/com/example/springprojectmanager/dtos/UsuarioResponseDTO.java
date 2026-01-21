package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.StatusUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String senha,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        StatusUsuario status) {
}
