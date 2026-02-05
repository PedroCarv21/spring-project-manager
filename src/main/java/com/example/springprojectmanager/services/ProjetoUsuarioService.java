package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.*;
import com.example.springprojectmanager.entities.chavesprimariascompostas.ProjetoUsuarioId;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
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

    public ProjetoUsuario salvar(Projeto projeto, Usuario usuario, Role role){
        ProjetoUsuario projetoUsuario = new ProjetoUsuario(new ProjetoUsuarioId(), projeto, usuario, role);
        return this.projetoUsuarioRepository.save(projetoUsuario);
    }

    public ProjetoUsuario salvar(ProjetoUsuario projetoUsuario){
        return this.projetoUsuarioRepository.save(projetoUsuario);
    }

    public ProjetoUsuario buscarProjetoUsuario(Projeto projeto, Usuario usuario){
        return this.projetoUsuarioRepository
                .findByProjetoAndUsuario(projeto, usuario)
                .orElseThrow(() -> new NaoEncontradoException("Usuário " + usuario.getNome() + " não pertence a este projeto."));
    }

    public void deletarProjetoUsuario(Projeto projeto, Usuario usuario){
        ProjetoUsuario projetoUsuario = this.buscarProjetoUsuario(projeto, usuario);
        this.projetoUsuarioRepository.delete(projetoUsuario);
    }

    public boolean existeProjetoUsuario(Projeto projeto, Usuario usuario){
        return this.projetoUsuarioRepository.existsByProjetoAndUsuario(projeto, usuario);
    }
}
