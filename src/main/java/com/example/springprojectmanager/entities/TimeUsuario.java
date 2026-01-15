package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.entities.chavesprimariascompostas.TimeUsuarioId;
import jakarta.persistence.*;

@Entity
@Table(name = "time_usuario")
public class TimeUsuario {

    @EmbeddedId
    private TimeUsuarioId id;

    @ManyToOne
    @MapsId("timeId")
    @JoinColumn(name = "time_id")
    private Time time;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public TimeUsuario() {
    }

    public TimeUsuario(TimeUsuarioId id, Time time, Usuario usuario) {
        this.id = id;
        this.time = time;
        this.usuario = usuario;
    }
}
