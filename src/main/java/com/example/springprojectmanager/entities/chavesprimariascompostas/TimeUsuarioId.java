package com.example.springprojectmanager.entities.chavesprimariascompostas;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TimeUsuarioId implements Serializable {

    @Column(name = "time_id", nullable = false)
    private UUID timeId;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimeUsuarioId that = (TimeUsuarioId) o;
        return Objects.equals(timeId, that.timeId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeId, usuarioId);
    }
}
