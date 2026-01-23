package com.example.springprojectmanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCadastroDTO {

    @NotBlank
    private String nome;
    @NotBlank
    @Size(min = 6)
    private String senha;
}
