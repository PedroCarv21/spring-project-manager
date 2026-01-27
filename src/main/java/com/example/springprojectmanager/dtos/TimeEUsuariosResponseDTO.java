package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.StatusTime;

import java.time.LocalDateTime;
import java.util.List;

public record TimeEUsuariosResponseDTO(
        String nome,
        StatusTime status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        String nomeProjeto,
        List<UsuarioRoleResponseDTO> usuarioRoleResponseDTOList) {
}
