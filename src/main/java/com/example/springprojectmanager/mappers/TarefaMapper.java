package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.TarefaResponseDTO;
import com.example.springprojectmanager.dtos.TarefaUsuariosResponseDTO;
import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.enums.StatusTarefaAtualizacao;
import com.example.springprojectmanager.services.TarefaService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TarefaMapper {

    @Autowired
    TarefaService tarefaService;

    @Autowired
    UsuarioMapper usuarioMapper;

    @Mappings({
        @Mapping(target = "nomeProjeto", expression = "java( tarefa.getProjeto().getNome() )"),
        @Mapping(target = "nomeTime", expression = "java( tarefa.getTime() != null ? tarefa.getTime().getNome() : null )")
    })
    public abstract TarefaResponseDTO toDTO(Tarefa tarefa);

    @Mappings({
        @Mapping(target = "nomeProjeto", expression = "java( tarefa.getProjeto().getNome() )"),
        @Mapping(target = "nomeTime", expression = "java( tarefa.getTime() != null ? tarefa.getTime().getNome() : null )"),
        @Mapping(target = "usuarios", expression = "java( this.tarefaService.buscarUsuariosDaTarefa(tarefa.getProjeto().getId(), tarefa.getNome()).stream().map(u -> usuarioMapper.toUsuarioRoleResponseDTO(u, tarefa.getProjeto())).toList() )")
    })
    public abstract TarefaUsuariosResponseDTO toTarefaUsuariosResponseDTO(Tarefa tarefa);

    public abstract StatusTarefa toStatusTarefa(StatusTarefaAtualizacao statusTarefaAtualizacao);
}
