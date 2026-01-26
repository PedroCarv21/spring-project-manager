package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.UsuarioService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios/")
@RequiredArgsConstructor
@Validated
public class UsuarioController implements CriadorLocation{

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

//    @PostMapping
//    public ResponseEntity<Usuario> salvar(
//            @RequestBody
//            @Valid
//            UsuarioRequestDTO usuarioRequestDTO){
//        Usuario usuario = this.usuarioMapper.toEntity(usuarioRequestDTO);
//        Usuario usuarioSalvo = this.usuarioService.salvar(usuario);
//        return ResponseEntity
//                .created(gerarLocation(usuarioSalvo.getId()))
//                .body(usuarioSalvo);
//    }

    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> consultar(){
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        UsuarioResponseDTO usuarioResponseDTO = this.usuarioMapper.toDTO(usuarioAutenticado);
        return ResponseEntity.ok(usuarioResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @RequestParam(name = "nome", required = false)
            String novoNome,
            @Size(min = 7, message = "A senha deve ter no mín. 7 caracteres")
            @RequestParam(name = "senha", required = false)
            @Parameter(schema = @Schema(type = "string", format = "password"))
            String novaSenha
    ){
        Usuario usuario = this.usuarioService.atualizar(novoNome, novaSenha);
        UsuarioResponseDTO usuarioResponseDTO = this.usuarioMapper.toDTO(usuario);
        return ResponseEntity.ok(usuarioResponseDTO);
    }

    @DeleteMapping
    @PreAuthorize("@fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<Void> deletar(){
        this.usuarioService.deletar();
        return ResponseEntity.noContent().build();
    }
}
