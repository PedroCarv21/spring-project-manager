package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioRoleResponseDTO(
        UUID id,
        String nome,
        String email,
        String senha,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        StatusUsuario status,
        Role role) {
}
