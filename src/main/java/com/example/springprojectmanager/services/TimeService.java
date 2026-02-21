package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.*;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.ProjetoUsuarioRepository;
import com.example.springprojectmanager.repositories.TimeRepository;
import com.example.springprojectmanager.repositories.TimeUsuarioRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProjetoUsuarioRepository projetoUsuarioRepository;
    private final ProjetoUsuarioService projetoUsuarioService;
    private final TimeUsuarioService timeUsuarioService;
    private final TimeUsuarioRepository timeUsuarioRepository;
    private final UsuarioService usuarioService;
    private final TarefaUsuarioService tarefaUsuarioService;

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
            throw new NaoEncontradoException("Não existe um time chamado '" + nomeTime + "'.");
        }
        return projetos;
    }

    public Time salvar(String nomeProjeto, String nomeTime){

        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        this.projetoService.verificarStatusDoProjeto(projeto);

        boolean existeTimeComEsteNome = projeto.getTimes().stream().anyMatch(time -> time.getNome().equals(nomeTime));

        if (existeTimeComEsteNome){
            throw new ConflitoException("Já existe um time chamado '" + nomeTime + "' dentro do projeto '" + nomeProjeto + "'.");
        }

        Time time = new Time(nomeTime, projeto, StatusTime.ATIVO);
        Time timeSalvo = this.timeRepository.save(time);
        timeSalvo.getProjeto().getTimes().add(timeSalvo);
        return timeSalvo;
    }

    public Time adicionarParticipante(String nomeProjeto, String nomeTime, String username, Role role){
        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        this.projetoService.verificarStatusDoProjeto(projeto);

        Time time = this.capturarTime(projeto, nomeTime).orElseThrow(() -> new NaoEncontradoException("Este time não foi encontrado neste projeto."));

        this.verificarStatusDoTime(time);

        Usuario usuario = this.usuarioService.buscarPorNome(username);
        Optional<ProjetoUsuario> projetoUsuarioOptional = this.projetoUsuarioRepository.findByProjetoAndUsuario(projeto, usuario);
        if (projetoUsuarioOptional.isPresent()){
            throw new ConflitoException("O usuário já faz parte deste projeto");
        }
        this.projetoUsuarioService.salvar(projeto, usuario, role);
        this.timeUsuarioService.salvar(time, usuario);
        return time;
    }

    public Time atualizar(String nomeProjeto, String nomeAtualTime, String novoNomeTime){

        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        this.projetoService.verificarStatusDoProjeto(projeto);

        Time time = this.capturarTime(projeto, nomeAtualTime)
                .orElseThrow(() -> new NaoEncontradoException("Não existe nenhum time '" + nomeAtualTime + "' dentro do projeto '" + nomeProjeto + "'."));

        this.verificarStatusDoTime(time);
        if (novoNomeTime != null && !novoNomeTime.strip().equals("")){
            Optional<Time> timeComNovoNome = this.capturarTime(projeto, novoNomeTime);
            if (timeComNovoNome.isPresent() && !nomeAtualTime.equals(novoNomeTime)){
                throw new NaoEncontradoException("Já existe um time com o nome '" + novoNomeTime + "'.");
            }
            time.setNome(novoNomeTime);
        }
        return this.timeRepository.save(time);
    }


    public Time atualizarRoleParticipante(String nomeProjeto, String username, Role role){
        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        this.projetoService.verificarStatusDoProjeto(projeto);

        Usuario usuario = this.usuarioService.buscarPorNome(username);
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);
        if (projetoUsuario.getRole().equals(Role.ADMIN)){
            throw new ConflitoException("O administrador não pode mudar a sua própria ROLE");
        }
        projetoUsuario.setRole(role);
        this.projetoUsuarioService.salvar(projetoUsuario);
        List<Time> times = projeto.getTimes();
        for (Time time: times){
            Optional<Usuario> usuarioOptional = timeUsuarioService
                    .buscarUsuariosDoTime(time)
                    .stream()
                    .filter(u -> u.getNome().equals(username))
                    .findFirst();
            if (usuarioOptional.isPresent()){
                this.verificarStatusDoTime(time);
                return time;
            }
        }
        return null;
    }
    public Time reativar(String nomeProjeto, String nomeTime){
        Projeto projeto = this.projetoService.buscarPorNome(nomeProjeto);
        this.projetoService.verificarStatusDoProjeto(projeto);

        Time time = this.capturarTime(projeto, nomeTime).orElseThrow(() -> new ConflitoException("Time não encontrado."));
        time.setStatus(StatusTime.ATIVO);
        return this.timeRepository.save(time);
    }

    public void deletar(String nomeProjeto, String nomeTime){

        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        this.projetoService.verificarStatusDoProjeto(projeto);

        Optional<Time> timeOptional = this.capturarTime(projeto, nomeTime);
        if (timeOptional.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um time " + nomeTime + " dentro do projeto " + nomeProjeto);
        }
        Time time = timeOptional.get();
        this.verificarStatusDoTime(time);
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

    @Transactional
    public void excluirUsuario(String nomeProjeto, String nomeTime, String username){
        Projeto projeto = this.projetoService.capturarProjetoAdministradoPorVoce(nomeProjeto).get();

        Usuario usuario = this.usuarioService.buscarPorNome(username);
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);

        if (projetoUsuario.getRole().equals(Role.ADMIN)){
            throw new ConflitoException("Administrador não pode ser deletado do próprio projeto");

        }

        Time time = this.capturarTime(projeto, nomeTime).orElseThrow(() -> new ConflitoException("Time não encontrado"));

        List<Tarefa> tarefas = time.getTarefas();



        List<Tarefa> tarefasDoUsuario = tarefas
                .stream()
                .filter(t -> this.tarefaUsuarioService.existeTarefaUsuario(t, usuario) && !t.getStatus().equals(StatusTarefa.CANCELADO))
                .toList();


        if (!tarefasDoUsuario.isEmpty()){
            System.out.println(tarefasDoUsuario.size());
            StringBuilder nomesTarefas = new StringBuilder();
            for (Tarefa t: tarefasDoUsuario){
                nomesTarefas.append(" - ").append(t.getNome());
            }
            throw new ConflitoException("Este usuário ainda está vinculado com a(s) seguinte(s) tarefa(s) deste time:" + nomesTarefas);
        }
        tarefas
                .stream()
                .filter(t -> this.tarefaUsuarioService.existeTarefaUsuario(t, usuario))
                .forEach(t -> this.tarefaUsuarioService.desvincularTarefaDoUsuario(t, usuario));


        this.timeUsuarioService.deletarTimeUsuario(time, usuario);
        this.projetoUsuarioService.deletarProjetoUsuario(projeto, usuario);
    }

    public void verificarStatusDoTime(Time time){
        if (time.getStatus().equals(StatusTime.ENCERRADO)){
            throw new ConflitoException("Não é possível realizar essa ação pois o time '" + time.getNome() + "' foi encerrado.");
        }
    }
}
