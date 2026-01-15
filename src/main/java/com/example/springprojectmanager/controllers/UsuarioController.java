package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.UsuarioRequestDTO;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("usuarios/")
@RequiredArgsConstructor
public class UsuarioController implements CriadorLocation{

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

    @PostMapping
    public ResponseEntity<Usuario> salvar(
            @RequestBody
            @Valid
            UsuarioRequestDTO usuarioRequestDTO){
        Usuario usuario = this.usuarioMapper.toEntity(usuarioRequestDTO);
        Usuario usuarioSalvo = this.usuarioService.salvar(usuario);
        return ResponseEntity
                .created(gerarLocation(usuarioSalvo.getId()))
                .body(usuarioSalvo);
    }

    @GetMapping
    public ResponseEntity<Usuario> consultar(@RequestParam("nome") String nome){
        Usuario usuario = this.usuarioService.buscarPorNome(nome);
        System.out.println("Usuario autenticado: " + this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado());
        return ResponseEntity.ok(usuario);
    }
}
