package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.UsuarioCadastroDTO;
import com.example.springprojectmanager.dtos.UsuarioRequestDTO;
import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toDTO(Usuario usuario);

    @Mapping(target = "status", expression = "java( StatusUsuario.ATIVO )")
    Usuario toUsuarioCadastrado(UsuarioCadastroDTO usuarioCadastroDTO);
}
