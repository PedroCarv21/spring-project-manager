package com.example.springprojectmanager.security;

import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.UsuarioRepository;
import com.example.springprojectmanager.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username){

        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByNome(username);
        if (usuarioOptional.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um usuário chamado " + username);
        }
        Usuario usuario = usuarioOptional.get();

        return User
                .builder()
                .username(usuario.getNome())
                .password(usuario.getSenha())
                .build();
    }
}
