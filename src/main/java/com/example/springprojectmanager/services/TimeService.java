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

    public List<Projeto> pesquisar(String nomeProjeto, String nomeTime){

        List<Projeto> projetos = this.projetoService.pesquisar(nomeProjeto, null);

        if (nomeTime == null){
            return projetos;
        }

        for (Projeto projeto: projetos){
            Optional<Time> timeOptional = projeto.getTimes().stream().filter(time -> time.getNome().equals(nomeTime)).findFirst();
            if (timeOptional.isEmpty()){
                projeto.setTimes(List.of());
                continue;
            }
            Time time = timeOptional.get();
            projeto.setTimes(List.of(time));
        }

        projetos = projetos.stream().filter(projeto -> !projeto.getTimes().isEmpty()).toList();

        if (projetos.isEmpty()){
            throw new NaoEncontradoException("Não existe nenhum projeto com um time chamado '" + nomeTime + "'.");
        }
        return projetos;
    }

    public Time salvar(String nomeProjeto, String nomeTime){

        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado();

        Projeto projeto = this.projetoService.capturarProjeto(nomeProjeto).get();

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

    public Time atualizar(String nomeProjeto, String nomeAtualTime, String novoNomeTime, StatusTime statusTime){

        Projeto projeto = this.projetoService.capturarProjeto(nomeProjeto).get();

        Optional<Time> timeOptional = this.capturarTime(projeto, nomeAtualTime);

        if (timeOptional.isEmpty()){
            throw new NaoEncontradoException("Não existe nenhum time '" + nomeAtualTime + "' dentro do projeto '" + nomeProjeto + "'.");
        }
        Time time = timeOptional.get();
        if (novoNomeTime != null && !novoNomeTime.strip().equals("")){
            Optional<Time> timeComNovoNome = this.capturarTime(projeto, novoNomeTime);
            if (timeComNovoNome.isPresent() && !nomeAtualTime.equals(novoNomeTime)){
                throw new NaoEncontradoException("Já existe um time com o nome '" + novoNomeTime + "'.");
            }
            time.setNome(novoNomeTime);
        }
        if (statusTime != null){
            time.setStatus(statusTime);
        }
        return this.timeRepository.save(time);
    }

    public void deletar(String nomeProjeto, String nomeTime){

        Projeto projeto = this.projetoService.capturarProjeto(nomeProjeto).get();
        Optional<Time> timeOptional = this.capturarTime(projeto, nomeTime);
        if (timeOptional.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um time " + nomeTime + " dentro do projeto " + nomeProjeto);
        }
        Time time = timeOptional.get();
        if (time.getStatus().equals(StatusTime.ENCERRADO)){
            throw new ConflitoException("Esse time já foi encerrado.");
        }
        time.setStatus(StatusTime.ENCERRADO);
        this.timeRepository.save(time);
    }

    public Optional<Time> capturarTime(Projeto projeto, String nomeTime){
        return projeto.getTimes().stream().filter(time -> time.getNome().equals(nomeTime)).findFirst();
    }

    protected Time buscarPorId(UUID id){
        Optional<Time> timeOptional = this.timeRepository.findById(id);
        if (timeOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um time com este ID");
        }
        return timeOptional.get();
    }
}
