package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tarefas")
@Getter
@Setter
public class Tarefa extends BaseEntity{

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "time_id")
    private Time time;

    @OneToMany(mappedBy = "tarefa")
    private List<TarefaUsuario> usuarioRelacionados;



}
