package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.UsuarioCadastroDTO;
import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.dtos.UsuarioRoleResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.RoleParticipante;
import com.example.springprojectmanager.services.ProjetoUsuarioService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UsuarioMapper {

    @Autowired
    ProjetoUsuarioService projetoUsuarioService;

    public abstract UsuarioResponseDTO toDTO(Usuario usuario);

    @Mapping(target = "status", expression = "java( StatusUsuario.ATIVO )")
    public abstract Usuario toUsuarioCadastrado(UsuarioCadastroDTO usuarioCadastroDTO);

    public abstract Role toRole(RoleParticipante roleParticipante);

    @Mappings({
        @Mapping(target = "id", source = "usuario.id"),
        @Mapping(target = "nome", source = "usuario.nome"),
        @Mapping(target = "dataCriacao", source = "usuario.dataCriacao"),
        @Mapping(target = "dataAtualizacao", source = "usuario.dataAtualizacao"),
        @Mapping(target = "status", source = "usuario.status"),
        @Mapping(target = "role", expression = "java( this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario).getRole() )")
    })
    public abstract UsuarioRoleResponseDTO toUsuarioRoleResponseDTO(Usuario usuario, Projeto projeto);
}
