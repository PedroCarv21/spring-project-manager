package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {
}
