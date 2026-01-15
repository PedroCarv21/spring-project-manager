package com.example.springprojectmanager.entities.chavesprimariascompostas;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjetoUsuarioId implements Serializable {

    @Column(name = "projeto_id", nullable = false)
    private UUID projetoId;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjetoUsuarioId that = (ProjetoUsuarioId) o;
        return Objects.equals(projetoId, that.projetoId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projetoId, usuarioId);
    }
}
