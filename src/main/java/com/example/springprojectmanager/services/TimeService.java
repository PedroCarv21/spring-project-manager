package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.TimeRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeService {

    private final TimeRepository timeRepository;
    private final ProjetoRepository projetoRepository;
    private final ProjetoService projetoService;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;
    private final ProjetoUsuarioService projetoUsuarioService;
    private final TimeUsuarioService timeUsuarioService;

    public List<Time> pesquisar(String nomeProjeto, String nomeTime){
        Optional<Projeto> projetoOptional = this.projetoRepository.findByNome(nomeProjeto);
        if (projetoOptional.isEmpty()){
            throw new NaoEncontradoException("Não há nenhum projeto chamado " + nomeProjeto);
        }
        Projeto projeto = projetoOptional.get();
        if (nomeTime == null){
            return projeto.getTimes();
        }
        Optional<Time> time = this.timeRepository.findByNome(nomeTime);
        if (time.isEmpty()){
            throw new NaoEncontradoException("Não há nenhum time chamado " + nomeTime);
        }
        return List.of(time.get());
    }

    public Time salvar(String nomeProjeto, String nomeTime){

        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);

        Projeto projeto = this.projetoService.capturarProjetoDaLista(projetos, nomeProjeto);

        if (projeto.getStatus().equals(StatusProjeto.CANCELADO) || projeto.getStatus().equals(StatusProjeto.CONCLUIDO)){
            throw new ConflitoException("Não é possível criar um time em um projeto que já foi " + projeto.getStatus().toString());
        }

        boolean existeTimeComEsteNome = projeto.getTimes().stream().anyMatch(time -> time.getNome().equals(nomeTime));

        if (existeTimeComEsteNome){
            throw new ConflitoException("Já existe um time chamado '" + nomeTime + "' dentro do projeto '" + nomeProjeto + "'.");
        }

        Time time = new Time(nomeTime, projeto, StatusTime.ATIVO);
        Time timeSalvo = this.timeRepository.save(time);
        this.timeUsuarioService.salvar(timeSalvo, usuarioAutenticado);
        timeSalvo.getProjeto().getTimes().add(timeSalvo);
        return timeSalvo;
    }

    public void deletar(String nomeProjeto, String nomeTime){
        Projeto projeto = this.projetoService.buscarPorNome(nomeProjeto);
        List<Time> times = projeto.getTimes();
        boolean timeExiste = times.stream().anyMatch(time -> time.getNome().equals(nomeTime));
        if (!timeExiste){
            throw new NaoEncontradoException("Não foi encontrado um time " + nomeTime + " dentro do projeto " + nomeProjeto);
        }
        times.forEach(time -> {
            if (time.getNome().equals(nomeTime)){
                if (time.getStatus().equals(StatusTime.ENCERRADO)){
                    throw new ConflitoException("Esse time já foi encerrado.");
                }
                time.setStatus(StatusTime.ENCERRADO);
                this.timeRepository.save(time);
            }
        });
    }

    public Time atualizar(UUID id, String nome, StatusTime status){
        Time time = this.buscarPorId(id);

        if (time.getProjeto().getStatus().equals(StatusProjeto.CANCELADO) || time.getProjeto().getStatus().equals(StatusProjeto.CONCLUIDO)){
            throw new ConflitoException("Não é possível atualizar um time que pertence a um projeto já " + time.getProjeto().getStatus());
        }

        time.setNome(nome);
        time.setStatus(status);
        return this.timeRepository.save(time);
    }

    protected Time buscarPorId(UUID id){
        Optional<Time> timeOptional = this.timeRepository.findById(id);
        if (timeOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um time com este ID");
        }
        return timeOptional.get();
    }
}
