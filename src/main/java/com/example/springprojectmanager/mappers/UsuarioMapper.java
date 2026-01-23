package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.UsuarioCadastroDTO;
import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {


    UsuarioResponseDTO toDTO(Usuario usuario);

    @Mapping(target = "status", expression = "java( StatusUsuario.ATIVO )")
    Usuario toUsuarioCadastrado(UsuarioCadastroDTO usuarioCadastroDTO);
}
