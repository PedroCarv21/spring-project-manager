package com.example.springprojectmanager.controllers;

import com.example.springprojectmanager.dtos.TimeResponseDTO;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.mappers.TimeMapper;
import com.example.springprojectmanager.services.TimeService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<TimeResponseDTO>> pesquisar(
            @RequestParam(name = "nome_projeto")
            @NotBlank(message = "Informe pelo menos o nome de um projeto.")
            String nomeProjeto,
            @RequestParam(name = "nome_time", required = false)
            String nomeTime){

        List<Time> timeList = this.timeService.pesquisar(nomeProjeto, nomeTime);
        List<TimeResponseDTO> timeResponseDTOList = timeList
                .stream()
                .map(this.timeMapper::toDTO)
                .toList();

        return ResponseEntity.ok(timeResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<TimeResponseDTO> salvar(
            @NotBlank(message = "Informe um nome para o novo projeto.")
            @RequestParam("nome_projeto") String nomeProjeto,
            @NotBlank(message = "Informe um nome para o novo time.")
            @RequestParam("nome_time") String nomeTime
    ){
        Time timeSalvo = this.timeService.salvar(nomeProjeto, nomeTime);
        TimeResponseDTO timeResponseDTO = this.timeMapper.toDTO(timeSalvo);

        return ResponseEntity.created(gerarLocation(timeSalvo.getId())).body(timeResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeResponseDTO> atualizar(
            @PathVariable("id") UUID id,
            @NotBlank(message = "Informe um nome para o novo time.")
            @RequestParam("nome") String nome,
            @RequestParam("status") StatusTime status){

        Time timeAtualizado = this.timeService.atualizar(id, nome, status);
        TimeResponseDTO timeResponseDTO = this.timeMapper.toDTO(timeAtualizado);
        return ResponseEntity.ok(timeResponseDTO);
    }

    @DeleteMapping
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
