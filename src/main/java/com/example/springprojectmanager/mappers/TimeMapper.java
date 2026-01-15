package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.TimeResponseDTO;
import com.example.springprojectmanager.entities.Time;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeMapper {

    @Mapping(target = "nomeProjeto", expression = "java( time.getProjeto().getNome() )")
    TimeResponseDTO toDTO(Time time);
}
