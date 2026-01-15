package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario buscarPorNome(String nome){
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByNome(nome);
        if (usuarioOptional.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um usuário chamado " + nome);
        }

        return usuarioOptional.get();
    }

    public Usuario salvar(Usuario usuario){
        usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
//        usuario.setSenha(this.criarEncoder().encode(usuario.getSenha()));
        return this.usuarioRepository.save(usuario);
    }

//    public PasswordEncoder criarEncoder(){
//        return new BCryptPasswordEncoder(10);
//    }
}
