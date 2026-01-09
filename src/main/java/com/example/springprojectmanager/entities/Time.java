package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "times")
public class Time extends BaseEntity{

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusTime status;

    @ManyToOne
    @JoinColumn(name = "id_projeto")
    private Projeto projeto;

    public Time() {
    }

    public Time(String nome, Projeto projeto, StatusTime status) {
        super(nome);
        this.projeto = projeto;
        this.status = status;
    }
}
