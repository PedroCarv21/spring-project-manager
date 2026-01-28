package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.*;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.TarefaRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final ProjetoService projetoService;
    private final TimeService timeService;
    private final UsuarioService usuarioService;
    private final TimeUsuarioService timeUsuarioService;
    private final ProjetoUsuarioService projetoUsuarioService;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;

    public Tarefa salvar(UUID id, String nomeTime, String nomeTarefa, String descricao){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        boolean existeTarefaComEsteNome = projeto.getTarefas().stream().anyMatch(t -> t.getNome().equals(nomeTarefa));
        if (existeTarefaComEsteNome){
            throw new ConflitoException("Já existe uma tarefa chamada '" + nomeTarefa + "' neste projeto.");
        }

        Usuario usuarioAutenticado = fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuarioAutenticado);
        if (projetoUsuario.getRole().equals(Role.MANAGER) && nomeTime == null){
            throw new ConflitoException("Você como gerente deve informar o nome do seu time.");
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
            if (projetoUsuario.getRole().equals(Role.MANAGER)){
                this.timeUsuarioService.buscarTimeUsuario(time, usuarioAutenticado);
            }
            tarefa.setTime(time);
        }
        return this.tarefaRepository.save(tarefa);
    }

    public Tarefa atualizar(UUID id, String nomeTime, String antigoNomeTarefa, String novoNomeTarefa, String descricao, StatusTarefa statusTarefa){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        Tarefa tarefa = projeto
                .getTarefas()
                .stream()
                .filter(t -> t.getNome().equals(antigoNomeTarefa))
                .findFirst()
                .orElseThrow(() -> new ConflitoException("Não existe uma tarefa com o nome '" + antigoNomeTarefa + "'."));

        if (novoNomeTarefa != null && !novoNomeTarefa.strip().equals("")){
            boolean existeTarefaComEsteNome = projeto.getTarefas().stream().anyMatch(t -> t.getNome().equals(novoNomeTarefa));
            if (existeTarefaComEsteNome){
                throw new ConflitoException("O nome '" + novoNomeTarefa + "' já está em uso para uma das tarefas deste projeto.");
            }
            tarefa.setNome(novoNomeTarefa);
        }

        if (nomeTime != null && !nomeTime.strip().equals("")) {
            Time time = this.timeService
                    .capturarTime(projeto, nomeTime)
                    .orElseThrow(() -> new ConflitoException("Este time não existe."));
            tarefa.setTime(time);
        }

        if (descricao != null && !descricao.strip().equals("")){
            tarefa.setDescricao(descricao);
        }

        if (statusTarefa != null){
            tarefa.setStatus(statusTarefa);
        }

        return this.tarefaRepository.save(tarefa);
    }
}
