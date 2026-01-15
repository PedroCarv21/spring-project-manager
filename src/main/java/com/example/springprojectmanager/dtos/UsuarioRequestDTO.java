package com.example.springprojectmanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome vazio")
        String nome,
        @Size(min = 7, message = "Senha deve ter no min. 7 caracteres")
        String senha) {
}
