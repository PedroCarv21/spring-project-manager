package com.example.springprojectmanager.repositories;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.ProjetoUsuarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetoUsuarioRepository extends JpaRepository<ProjetoUsuario, ProjetoUsuarioId> {

    List<ProjetoUsuario> findByUsuario(Usuario usuario);

    boolean existsByProjetoAndUsuario(Projeto projeto, Usuario usuario);
}
