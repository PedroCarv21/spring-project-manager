package com.example.springprojectmanager.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjetoTimeReponseDTO(
        UUID id,
        String nome,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        List<TimeResponseDTO> times) {
}
