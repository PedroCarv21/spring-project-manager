package com.example.springprojectmanager.dtos;

import com.example.springprojectmanager.enums.StatusTarefa;

import java.time.LocalDateTime;
import java.util.List;

public record TarefaUsuariosResponseDTO(
        String nome,
        String descricao,
        StatusTarefa status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        String nomeProjeto,
        String nomeTime,
        List<UsuarioRoleResponseDTO> usuarios) {
}
