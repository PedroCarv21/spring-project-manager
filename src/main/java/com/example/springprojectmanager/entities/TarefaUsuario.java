package com.example.springprojectmanager.entities;

import com.example.springprojectmanager.entities.chavesprimariascompostas.TarefaUsuarioId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tarefa_usuario")
@Getter
@Setter
public class TarefaUsuario {

    @EmbeddedId
    private TarefaUsuarioId tarefaUsuarioId;

    @ManyToOne
    @MapsId("tarefaId")
    @JoinColumn(name = "tarefa_id")
    private Tarefa tarefa;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
