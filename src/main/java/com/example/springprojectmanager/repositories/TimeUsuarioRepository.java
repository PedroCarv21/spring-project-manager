package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.TimeUsuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TimeUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeUsuarioRepository extends JpaRepository<TimeUsuario, TimeUsuarioId> {
}
