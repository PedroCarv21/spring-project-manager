package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.ProjetoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjetoUsuarioService {

    private final ProjetoUsuarioRepository projetoUsuarioRepository;
    private final ProjetoRepository projetoRepository;

    public List<Projeto> listarProjetosDoUsuarioAutenticado(Usuario usuario){
        List<ProjetoUsuario> projetoUsuarioList = listarPorUsuario(usuario);
        return projetoUsuarioList.stream()
                .map(ProjetoUsuario::getProjeto)
                .toList();
    }

    public List<ProjetoUsuario> listarPorUsuario(Usuario usuario){
        return this.projetoUsuarioRepository.findByUsuario(usuario);
    }
}
