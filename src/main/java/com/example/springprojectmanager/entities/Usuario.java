package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.enums.StatusUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario extends BaseEntity{

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<TimeUsuario> timesRelacionados;

    @OneToMany(mappedBy = "usuario")
    private List<ProjetoUsuario> ProjetosRealacionados;

    @Enumerated(EnumType.STRING)
    private StatusUsuario status;

    public Usuario() {
    }

    public Usuario(String nome, String senha) {
        super(nome);
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", senha='" + senha + '\'' +
                ", timesRelacionados=" + timesRelacionados +
                ", ProjetosRealacionados=" + ProjetosRealacionados +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}