package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjetoMapper {

    StatusProjeto toStatusProjeto(StatusProjetoAtualizacao statusProjetoAtualizacao);
}
