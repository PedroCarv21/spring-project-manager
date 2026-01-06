package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "projetos")
public class Projeto extends BaseEntity{

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusProjeto statusProjeto;

    public Projeto(){

    }

    public Projeto(String nome, StatusProjeto statusProjeto) {
        super(nome);
        this.statusProjeto = statusProjeto;
    }
}
