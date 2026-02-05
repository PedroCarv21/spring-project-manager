package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.TimeEUsuariosResponseDTO;
import com.example.springprojectmanager.dtos.TimeResponseDTO;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.enums.StatusTarefaAtualizacao;
import com.example.springprojectmanager.services.TimeUsuarioService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TimeMapper {

    @Autowired
    TimeUsuarioService timeUsuarioService;
    @Autowired
    UsuarioMapper usuarioMapper;

    @Mapping(target = "nomeProjeto", expression = "java( time.getProjeto().getNome() )")
    public abstract TimeResponseDTO toDTO(Time time);

    @Mappings ({
        @Mapping(target = "usuarioRoleResponseDTOList", expression = "java(this.timeUsuarioService.buscarUsuariosDoTime(time).stream().map(usuario -> usuarioMapper.toUsuarioRoleResponseDTO(usuario, time.getProjeto())).toList() )"),
        @Mapping(target = "nomeProjeto", expression = "java( time.getProjeto().getNome() )"),
    })
    public abstract TimeEUsuariosResponseDTO toTimeEUsuariosResponseDTO(Time time);
}
