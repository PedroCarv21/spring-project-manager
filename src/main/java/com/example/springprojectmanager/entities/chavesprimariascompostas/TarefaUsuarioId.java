package com.example.springprojectmanager.entities.chavesprimariascompostas;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TarefaUsuarioId {

    @Column(name = "tarefa_id", nullable = false)
    private UUID tarefaId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TarefaUsuarioId that)) return false;
        return Objects.equals(tarefaId, that.tarefaId) && Objects.equals(usuarioId, that.usuarioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tarefaId, usuarioId);
    }
}
