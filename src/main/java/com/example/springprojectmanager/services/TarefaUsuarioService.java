package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.entities.TarefaUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.repositories.TarefaUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TarefaUsuarioService {

    private final TarefaUsuarioRepository tarefaUsuarioRepository;

    public TarefaUsuario salvar(TarefaUsuario tarefaUsuario){
        return this.tarefaUsuarioRepository.save(tarefaUsuario);
    }

    public boolean existeTarefaUsuario(Tarefa tarefa, Usuario usuario){
        return this.tarefaUsuarioRepository.existsByTarefaAndUsuario(tarefa, usuario);
    }
    public void desvincularTarefaDoUsuario(Tarefa tarefa, Usuario usuario){
        this.tarefaUsuarioRepository.deleteByTarefaAndUsuario(tarefa, usuario);
    }
}
