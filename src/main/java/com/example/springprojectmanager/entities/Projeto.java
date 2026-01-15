package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "projetos")
public class Projeto extends BaseEntity{

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusProjeto status;

    @OneToMany(mappedBy = "projeto")
    private List<Time> times;

    @OneToMany(mappedBy = "projeto")
    private List<ProjetoUsuario> UsuariosRelacionados;

    public Projeto(){

    }

    public Projeto(String nome, StatusProjeto status) {
        super(nome);
        this.status = status;
    }
}
