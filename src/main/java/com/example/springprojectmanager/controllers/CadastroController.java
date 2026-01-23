package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.UsuarioCadastroDTO;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CadastroController {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;

    @GetMapping("/cadastro")
    public String cadastro(Model model){
        model.addAttribute("usuario", new UsuarioCadastroDTO());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid
            @ModelAttribute("usuario")
            UsuarioCadastroDTO usuarioCadastroDTO,
            BindingResult result,
            Model model){
        if (result.hasErrors()){
            return "cadastro";
        }
        try{
            Usuario usuario = this.usuarioMapper.toUsuarioCadastrado(usuarioCadastroDTO);
            this.usuarioService.salvar(usuario);
            return "redirect:/login";
        }
        catch (ConflitoException e){
            model.addAttribute("erroNegocio", e.getMessage());
            return "cadastro";
        }
    }
}
