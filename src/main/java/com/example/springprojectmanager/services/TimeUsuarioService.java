package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.entities.TimeUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TimeUsuarioId;
import com.example.springprojectmanager.repositories.TimeUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeUsuarioService {

    private final TimeUsuarioRepository timeUsuarioRepository;

    public void salvar(Time time, Usuario usuario){
        TimeUsuario timeUsuario = new TimeUsuario(new TimeUsuarioId(), time, usuario);
        this.timeUsuarioRepository.save(timeUsuario);
    }

    public List<Usuario> buscarUsuariosDoTime(Time time){
        List<TimeUsuario> timeUsuarioList = this.timeUsuarioRepository.findByTime(time);
        return timeUsuarioList.stream().map(TimeUsuario::getUsuario).toList();
    }

//    public List<TimeUsuario> buscarTodos(){
//        List<TimeUsuario> timeUsuarioList = this.timeUsuarioRepository.findAll();
//        return timeUsuarioList;
//    }
//
//    public boolean existeUsuarioNesteTime(Time time, Usuario usuario){
//        return this.buscarTodos()
//                .stream()
//                .anyMatch(timeUsuario -> timeUsuario.getUsuario().getId().equals(usuario.getId()) && timeUsuario.getTime().getId().equals(time.getId()));
//    }
}
