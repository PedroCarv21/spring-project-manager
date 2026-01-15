package com.example.springprojectmanager.security;

import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FornecedorUsuarioAutenticado {

    private final UsuarioService usuarioService;

    public Usuario fornecerUsuarioAutenticado(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return this.usuarioService.buscarPorNome(userDetails.getUsername());
    }
}
