package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.TarefaResponseDTO;
import com.example.springprojectmanager.entities.Tarefa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TarefaMapper {

    @Mappings({
        @Mapping(target = "nomeProjeto", expression = "java( tarefa.getProjeto().getNome() )"),
        @Mapping(target = "nomeTime", expression = "java( tarefa.getTime() != null ? tarefa.getTime().getNome() : null )")
    })
    TarefaResponseDTO toDTO(Tarefa tarefa);
}
