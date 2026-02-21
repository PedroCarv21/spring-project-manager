package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.UsuarioResponseDTO;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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

    @GetMapping
    @Operation(
            description = """
                    Retorna os dados do usuário, tais como: nome, e-mail, senha (criptografada com bcrypt),
                    data de criação da conta, data de atualização da conta e o status (que pode ser ATIVO ou DESATIVADO).
                    """)
    public ResponseEntity<UsuarioResponseDTO> consultar(){
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        UsuarioResponseDTO usuarioResponseDTO = this.usuarioMapper.toDTO(usuarioAutenticado);
        return ResponseEntity.ok(usuarioResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza o nome, e-mail e/ou senha do usuário. Não é possível escolher um nome de usuário e/ou e-mail que já estejam
                    em uso por outra conta.  Quando a atualização for bem-sucedida, além dos campos que receberam os novos dados, o campo
                    que informa a data de última atualização também será alterada.
                    """)
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @RequestParam(name = "nome", required = false)
            String novoNome,
            @Email(message = "Informe um e-mail válido.")
            @RequestParam(name = "email", required = false)
            String novoEmail,
            @Size(min = 7, message = "A senha deve ter no mín. 7 caracteres.")
            @RequestParam(name = "senha", required = false)
            @Parameter(schema = @Schema(type = "string", format = "password"))
            String novaSenha
    ){
        Usuario usuario = this.usuarioService.atualizar(novoNome, novoEmail, novaSenha);
        UsuarioResponseDTO usuarioResponseDTO = this.usuarioMapper.toDTO(usuario);
        return ResponseEntity.ok(usuarioResponseDTO);
    }

    @DeleteMapping
    @PreAuthorize("@fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    @Operation(
            description = """
                    Atualiza os status do usuário para DESATIVADO. Isso impede que o usuário autenticado acesse boa parte dos
                    endpoints da aplicação. Caso deseje atualizar o status do usuário como ativo, acesse o link http://localhost:8080/logout
                    e faça o Login novamente.
                    """)
    public ResponseEntity<Void> deletar(){
        this.usuarioService.deletar();
        return ResponseEntity.noContent().build();
    }
}
