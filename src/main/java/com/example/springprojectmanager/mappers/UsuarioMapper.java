package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.UsuarioRequestDTO;
import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toDTO(Usuario usuario);
}
