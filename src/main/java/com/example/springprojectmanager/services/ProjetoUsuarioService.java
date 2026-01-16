package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.ProjetoUsuarioRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoUsuarioService {

    private final ProjetoUsuarioRepository projetoUsuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

    public List<Projeto> listarProjetosDoUsuarioAutenticado(){
        List<ProjetoUsuario> projetoUsuarioList = listarPorUsuario();
        return projetoUsuarioList.stream()
                .map(ProjetoUsuario::getProjeto)
                .toList();
    }

    public List<ProjetoUsuario> listarPorUsuario(){
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        return this.projetoUsuarioRepository.findByUsuario(usuarioAutenticado);
    }
}
