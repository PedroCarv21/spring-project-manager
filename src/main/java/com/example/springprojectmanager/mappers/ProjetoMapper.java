package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    StatusProjeto toStatusProjeto(StatusProjetoAtualizacao statusProjetoAtualizacao);

    ProjetoResponseDTO toDTO(Projeto projeto);
}
