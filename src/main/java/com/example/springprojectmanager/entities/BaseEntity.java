package com.example.springprojectmanager.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    protected UUID id;
    @Column(name = "nome", nullable = false)
    protected String nome;
    @Column(name = "data_criacao")
    @CreatedDate
    protected LocalDateTime dataCriacao;
    @Column(name = "data_atualizacao")
    @LastModifiedDate
    protected LocalDateTime dataAtualizacao;

    protected BaseEntity() {
    }

    protected BaseEntity(String nome) {
        this.nome = nome;
    }
}
