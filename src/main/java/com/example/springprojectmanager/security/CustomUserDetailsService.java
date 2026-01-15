package com.example.springprojectmanager.security;

import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username){

        Usuario usuario = this.usuarioService.buscarPorNome(username);

        return User
                .builder()
                .username(usuario.getNome())
                .password(usuario.getSenha())
                .build();
    }
}
