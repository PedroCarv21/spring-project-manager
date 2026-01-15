package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.entities.TimeUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TimeUsuarioId;
import com.example.springprojectmanager.repositories.TimeUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeUsuarioService {

    private final TimeUsuarioRepository timeUsuarioRepository;

    public void salvar(Time time, Usuario usuario){
        TimeUsuario timeUsuario = new TimeUsuario(new TimeUsuarioId(), time, usuario);
        this.timeUsuarioRepository.save(timeUsuario);
    }
}
