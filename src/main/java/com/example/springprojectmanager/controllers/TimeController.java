package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.ProjetoTimeReponseDTO;
import com.example.springprojectmanager.dtos.TimeEUsuariosResponseDTO;
import com.example.springprojectmanager.dtos.TimeResponseDTO;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.RoleParticipante;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.mappers.ProjetoMapper;
import com.example.springprojectmanager.mappers.TimeMapper;
import com.example.springprojectmanager.mappers.UsuarioMapper;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import com.example.springprojectmanager.services.ProjetoService;
import com.example.springprojectmanager.services.TimeService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
@Validated
public class TimeController implements CriadorLocation{

    private final TimeService timeService;
    private final TimeMapper timeMapper;
    private final ProjetoService projetoService;
    private final ProjetoMapper projetoMapper;
    private final ProjetoRepository projetoRepository;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
//    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeProjeto)")
    public ResponseEntity<List<ProjetoTimeReponseDTO>> pesquisar(
            @NotBlank(message = "Informe o nome do projeto")
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time", required = false)
            String nomeTime){

        List<Projeto> projetos = this.timeService.pesquisar(nomeProjeto, nomeTime);
        List<ProjetoTimeReponseDTO> projetoTimeReponseDTOList = projetos.stream().map(this.projetoMapper::toProjetoTimeResponseDTO).toList();
        return ResponseEntity.ok(projetoTimeReponseDTOList);
    }

    @PostMapping
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<ProjetoTimeReponseDTO> salvar(
            @NotBlank(message = "Informe um nome para o novo projeto.")
            @RequestParam("nome_projeto") String nomeProjeto,
            @NotBlank(message = "Informe um nome para o novo time.")
            @RequestParam("nome_time") String nomeTime
    ){
        Time timeSalvo = this.timeService.salvar(nomeProjeto, nomeTime);

        ProjetoTimeReponseDTO projetoTimeResponseDTO = this.projetoMapper.toProjetoTimeResponseDTO(timeSalvo.getProjeto());
        return ResponseEntity.created(gerarLocation(timeSalvo.getId())).body(projetoTimeResponseDTO);
    }

    @PutMapping
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<TimeResponseDTO> atualizar(
            @NotBlank(message = "Informe o nome do projeto.")
            @RequestParam("nome_projeto") String nomeProjeto,
            @NotBlank(message = "Informe o nome atual do time.")
            @RequestParam("nome_atual_time") String nomeAtualTime,
            @RequestParam(value = "novo_nome_time", required = false) String novoNomeTime,
            @RequestParam(value = "status", required = false) StatusTime status){

        Time timeAtualizado = this.timeService.atualizar(nomeProjeto, nomeAtualTime, novoNomeTime, status);
        TimeResponseDTO timeResponseDTO = this.timeMapper.toDTO(timeAtualizado);
        return ResponseEntity.ok(timeResponseDTO);
    }

    @PostMapping("/novosparticipantes")
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<TimeEUsuariosResponseDTO> adicionarUsuario(
            @RequestParam(name = "nome_projeto")
            String nomeProjeto,
            @RequestParam(name = "nome_time")
            String nomeTime,
            @RequestParam(name = "username")
            String username,
            @RequestParam(name = "role")
            RoleParticipante roleParticipante){

        Role role = this.usuarioMapper.toRole(roleParticipante);
        Time time = this.timeService.adicionarUsuario(nomeProjeto, nomeTime, username, role);
        TimeEUsuariosResponseDTO timeEUsuariosResponseDTO = this.timeMapper.toTimeEUsuariosResponseDTO(time);
        return ResponseEntity.ok(timeEUsuariosResponseDTO);
    }

    @DeleteMapping
    @PreAuthorize("@projetoService.possuiAutorizacaoParaAtualizar(#nomeProjeto) and @fornecedorUsuarioAutenticado.permaneceComContaAtiva()")
    public ResponseEntity<Void> deletar(
            @NotBlank(message = "Informe o nome de um projeto")
            @RequestParam("nome_projeto")
            String nomeProjeto,
            @NotBlank(message = "Informe o nome de um time")
            @RequestParam("nome_time")
            String nomeTime){
        this.timeService.deletar(nomeProjeto, nomeTime);
        return ResponseEntity.noContent().build();
    }


}
