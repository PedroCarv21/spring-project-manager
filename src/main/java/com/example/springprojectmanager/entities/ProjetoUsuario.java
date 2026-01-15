package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.entities.chavesprimariascompostas.ProjetoUsuarioId;
import com.example.springprojectmanager.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projeto_usuario")
@Getter
@Setter
public class ProjetoUsuario {

    @EmbeddedId
    private ProjetoUsuarioId id;

    @ManyToOne
    @MapsId("projetoId")
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private Role role;

    public ProjetoUsuario() {
    }

    public ProjetoUsuario(ProjetoUsuarioId id, Projeto projeto, Usuario usuario, Role role) {
        this.id = id;
        this.projeto = projeto;
        this.usuario = usuario;
        this.role = role;
    }
}
