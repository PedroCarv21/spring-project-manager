package com.example.springprojectmanager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario extends BaseEntity{

    @Column(name = "senha")
    private String senha;

    @OneToMany(mappedBy = "usuario")
    private List<TimeUsuario> timesRelacionados;

    @OneToMany(mappedBy = "usuario")
    private List<ProjetoUsuario> ProjetosRealacionados;

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