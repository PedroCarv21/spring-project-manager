package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.Tarefa;
import com.example.springprojectmanager.entities.Time;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final ProjetoService projetoService;
    private final TimeService timeService;

    public Tarefa salvar(UUID id, String nomeTime, String nomeTarefa, String descricao){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        boolean existeTarefaComEsteNome = projeto.getTarefas().stream().anyMatch(t -> t.getNome().equals(nomeTarefa));
        if (existeTarefaComEsteNome){
            throw new ConflitoException("Já existe uma tarefa chamada '" + nomeTarefa + "' neste projeto.");
        }
        Tarefa tarefa = new Tarefa();
        tarefa.setNome(nomeTarefa);
        tarefa.setDescricao(descricao);
        tarefa.setStatus(StatusTarefa.INICIADO);
        tarefa.setProjeto(projeto);
        if (nomeTime != null && !nomeTime.strip().equals("")){
            Time time = this.timeService
                    .capturarTime(projeto, nomeTime)
                    .orElseThrow(() -> new NaoEncontradoException("O time '" + nomeTime + "' não foi encontrado neste projeto."));
            tarefa.setTime(time);
        }
        return this.tarefaRepository.save(tarefa);
    }
}
