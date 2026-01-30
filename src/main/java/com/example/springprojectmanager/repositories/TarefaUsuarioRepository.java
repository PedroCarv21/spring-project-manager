package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.entities.TarefaUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TarefaUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaUsuarioRepository extends JpaRepository<TarefaUsuario, TarefaUsuarioId> {

    boolean existsByTarefaAndUsuario(Tarefa tarefa, Usuario usuario);

    void deleteByTarefaAndUsuario(Tarefa tarefa, Usuario usuario);
}
