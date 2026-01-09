package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.Time;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TimeRepository extends JpaRepository<Time, UUID> {

    Optional<Time> findByNome(String nome);
}
