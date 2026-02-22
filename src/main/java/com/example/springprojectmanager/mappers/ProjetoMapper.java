package com.example.springprojectmanager.mappers;

import com.example.springprojectmanager.dtos.ProjetoResponseDTO;
import com.example.springprojectmanager.dtos.ProjetoTimeReponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusProjetoAtualizacao;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.ProjetoUsuarioService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = TimeMapper.class)
public abstract class ProjetoMapper {

    @Autowired
    FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

    @Autowired
    ProjetoUsuarioService projetoUsuarioService;

    public abstract StatusProjeto toStatusProjeto(StatusProjetoAtualizacao statusProjetoAtualizacao);

    @Mapping(target = "role", expression = "java( this.projetoUsuarioService.buscarProjetoUsuario(projeto, this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado()).getRole() )")
    public abstract ProjetoResponseDTO toDTO(Projeto projeto);

    @Mapping(target = "role", expression = "java( this.projetoUsuarioService.buscarProjetoUsuario(projeto, this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado()).getRole() )")
    public abstract ProjetoTimeReponseDTO toProjetoTimeResponseDTO(Projeto projeto);
}
