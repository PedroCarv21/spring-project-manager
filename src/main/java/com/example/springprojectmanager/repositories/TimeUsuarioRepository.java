package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.entities.TimeUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TimeUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeUsuarioRepository extends JpaRepository<TimeUsuario, TimeUsuarioId> {

    List<TimeUsuario> findByTime(Time time);
}
