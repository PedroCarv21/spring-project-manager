package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusProjeto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjetoTimeReponseDTO(
        UUID id,
        String nome,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        StatusProjeto status,
        Role role,
        List<TimeResponseDTO> times) {
}
