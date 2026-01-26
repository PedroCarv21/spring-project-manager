package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusUsuario;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.UsuarioRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;
    private final UserDetailsService userDetailsService;
    private final ProjetoService projetoService;

    public Usuario buscarPorNome(String nome){
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByNome(nome);
        if (usuarioOptional.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um usuário chamado " + nome);
        }

        return usuarioOptional.get();
    }

    public Usuario salvar(Usuario usuario){
        List<Usuario> usuarioList = this.usuarioRepository.findAll();
        boolean existeUsuarioComEsteNome = usuarioList.stream().anyMatch(u -> u.getNome().equals(usuario.getNome()));
        if (existeUsuarioComEsteNome){
            throw new ConflitoException("Já existe um usuário com este nome");
        }
        usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
        return this.usuarioRepository.save(usuario);
    }

    public Usuario atualizar(String novoNome, String novaSenha){

        List<Usuario> usuarioList = this.usuarioRepository.findAll();
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();

        if (novoNome != null && !novoNome.strip().equals("")){
            boolean existeUsuarioComEsteNome = usuarioList.stream().anyMatch(usuario -> usuario.getNome().equals(novoNome));

            if (existeUsuarioComEsteNome && !usuarioAutenticado.getNome().equals(novoNome)){
                throw new ConflitoException("Já existe um usuário com este nome.");
            }
            usuarioAutenticado.setNome(novoNome);
        }
        if (novaSenha != null && !novaSenha.strip().equals("")){

            usuarioAutenticado.setSenha(passwordEncoder.encode(novaSenha));
        }

        Usuario usuarioSalvo = this.usuarioRepository.save(usuarioAutenticado);

        UserDetails userDetailsAtualizado = userDetailsService.loadUserByUsername(usuarioAutenticado.getNome());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetailsAtualizado,
                authentication.getCredentials(),
                userDetailsAtualizado.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        return usuarioSalvo;
    }

    public void deletar(){
        Usuario usuarioAutenticado = fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        if (usuarioAutenticado.getStatus().equals(StatusUsuario.DESATIVADO)){
            throw new ConflitoException("Esse usuário já está com a conta desativada");
        }
        usuarioAutenticado.setStatus(StatusUsuario.DESATIVADO);
        List<Projeto> projetoList = usuarioAutenticado.getProjetosRealacionados().stream().map(ProjetoUsuario::getProjeto).toList();
        if (!projetoList.isEmpty()){
            projetoList.forEach(projeto -> this.projetoService.deletar(projeto.getNome()));
        }
    }

    @Transactional
    public void reativarUsuarioEProjetos(String username){
        Usuario usuarioAutenticado = this.usuarioRepository.findByNome(username).get();
        if (usuarioAutenticado.getStatus().equals(StatusUsuario.DESATIVADO)){
            usuarioAutenticado.setStatus(StatusUsuario.ATIVO);
            this.usuarioRepository.save(usuarioAutenticado);
            List<Projeto> projetos = this.projetoService.pesquisar(null, null);
            projetos.forEach(projeto -> this.projetoService.atualizar(projeto.getNome(), null, StatusProjeto.INICIADO));
        }
    }
}
