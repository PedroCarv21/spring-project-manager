package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.dtos.ProjetoTimeReponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TimeMapper.class)
public interface ProjetoMapper {

    StatusProjeto toStatusProjeto(StatusProjetoAtualizacao statusProjetoAtualizacao);

    ProjetoResponseDTO toDTO(Projeto projeto);

    ProjetoTimeReponseDTO toProjetoTimeResponseDTO(Projeto projeto);
}
