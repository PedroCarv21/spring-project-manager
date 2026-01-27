package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "times")
public class Time extends BaseEntity{

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusTime status;

    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @OneToMany(mappedBy = "time")
    private List<Tarefa> tarefas;

    @OneToMany(mappedBy = "time")
    private List<TimeUsuario> usuariosRelacionados;

    public Time() {
    }

    public Time(String nome, Projeto projeto, StatusTime status) {
        super(nome);
        this.projeto = projeto;
        this.status = status;
    }
}
