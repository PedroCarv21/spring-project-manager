package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.*;
import com.example.springprojectmanager.entities.chavesprimariascompostas.TarefaUsuarioId;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusTarefa;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.TarefaRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private final TarefaUsuarioService tarefaUsuarioService;
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

    @Transactional
    public Tarefa atualizar(UUID id, String nomeTime, String antigoNomeTarefa, String novoNomeTarefa, String descricao, StatusTarefa statusTarefa) throws AccessDeniedException {
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        Tarefa tarefa = this.buscarTarefa(id, antigoNomeTarefa);
        Usuario usuarioAutenticado = fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuarioAutenticado);
        if (projetoUsuario.getRole().equals(Role.MEMBER) && !this.tarefaUsuarioService.existeTarefaUsuario(tarefa, usuarioAutenticado)){
            throw new AccessDeniedException("Como membro, você deve estar vinculado a tarefa para poder atualizá-la.");
        }

        if (novoNomeTarefa != null && !novoNomeTarefa.strip().equals("")){
            this.usuarioTemAutorizacaoParaAtualizar(projeto, usuarioAutenticado);
            boolean existeTarefaComEsteNome = projeto.getTarefas().stream().anyMatch(t -> t.getNome().equals(novoNomeTarefa));
            if (existeTarefaComEsteNome){
                throw new ConflitoException("O nome '" + novoNomeTarefa + "' já está em uso para uma das tarefas deste projeto.");
            }
            tarefa.setNome(novoNomeTarefa);
        }

        if (nomeTime != null && !nomeTime.strip().equals("")) {
            if (!projetoUsuario.getRole().equals(Role.ADMIN)){
                throw new AccessDeniedException("Apenas o administrador decide qual será o time da tarefa.");
            }
            Time time = this.timeService
                    .capturarTime(projeto, nomeTime)
                    .orElseThrow(() -> new ConflitoException("Este time não existe."));

            if (tarefa.getTime() == null || !tarefa.getTime().getNome().equals(nomeTime)){
                tarefa.getUsuarioRelacionados()
                        .forEach(tu -> this.tarefaUsuarioService.desvincularTarefaDoUsuario(tarefa, tu.getUsuario()));
            }

            tarefa.setTime(time);
        }

        if (descricao != null && !descricao.strip().equals("")){
            this.usuarioTemAutorizacaoParaAtualizar(projeto, usuarioAutenticado);
            tarefa.setDescricao(descricao);
        }

        if (statusTarefa != null){
            tarefa.setStatus(statusTarefa);
        }

        return this.tarefaRepository.save(tarefa);
    }

    public boolean usuarioTemAutorizacaoParaAtualizar(Projeto projeto, Usuario usuario){
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);
        if (projetoUsuario.getRole().equals(Role.MEMBER)){
            throw new AccessDeniedException("Membros só podem atualizar o status da tarefa.");
        }
        return true;
    }

    public Tarefa vincularTarefaAUmParticipante(UUID id, String nomeTarefa, String username){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        Usuario usuario = this.usuarioService.buscarPorNome(username);
        ProjetoUsuario projetoUsuario = projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);

        if (projetoUsuario.getRole().equals(Role.ADMIN)){
            throw new ConflitoException("O administrador do projeto não deve estar vinculado a nenhuma tarefa");
        }

        Tarefa tarefa = this.buscarTarefa(id, nomeTarefa);

        if (!this.tarefaEUsuarioEstaoNoMesmoTime(projeto.getId(), tarefa.getNome(), usuario.getNome())){
            throw new ConflitoException("A tarefa e o usuário devem estar no mesmo time para que sejam vinculados um ao outro.");
        }
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        ProjetoUsuario projetoUsuarioAutenticado = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuarioAutenticado);
        if (projetoUsuarioAutenticado.getRole().equals(Role.MANAGER)){

            if (!this.tarefaEUsuarioEstaoNoMesmoTime(projeto.getId(), tarefa.getNome(), usuarioAutenticado.getNome())){
                throw new ConflitoException("Como gerente, você deve estar no mesmo time que a tarefa e o usuário que será vinculado a tarefa.");
            }
        }


        if (this.tarefaUsuarioService.existeTarefaUsuario(tarefa, usuario)){
            throw new ConflitoException("Este usuário já está vinculado a tarefa");
        }
        TarefaUsuario tarefaUsuario = new TarefaUsuario(new TarefaUsuarioId(), tarefa, usuario);
        this.tarefaUsuarioService.salvar(tarefaUsuario);
        return tarefa;
    }

    public Tarefa buscarTarefa(UUID id, String nomeTarefa){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        return projeto
                .getTarefas()
                .stream()
                .filter(tarefa -> tarefa.getNome().equals(nomeTarefa))
                .findFirst()
                .orElseThrow(() -> new NaoEncontradoException("Tarefa não encontrada."));
    }

    public List<Usuario> buscarUsuariosDaTarefa(UUID id, String nomeTarefa){
        Tarefa tarefa = this.buscarTarefa(id, nomeTarefa);
        return tarefa
                .getUsuarioRelacionados()
                .stream()
                .map(TarefaUsuario::getUsuario)
                .toList();
    }

    public boolean tarefaEUsuarioEstaoNoMesmoTime(UUID id, String nomeTarefa, String username){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        Usuario usuario = this.usuarioService.buscarPorNome(username);
        Tarefa tarefa = this.buscarTarefa(id, nomeTarefa);
        Time timeDaTarefa = tarefa.getTime();
        List<Time> times = projeto.getTimes();
        Time timeDoUsuario = null;
        for (Time t: times){
            Optional<TimeUsuario> timeUsuarioOptional = t.getUsuariosRelacionados()
                    .stream()
                    .filter(tu -> tu.getUsuario().getId().equals(usuario.getId()))
                    .findFirst();
            if (timeUsuarioOptional.isPresent()){
                timeDoUsuario = timeUsuarioOptional.get().getTime();
                break;
            }
        }

        return timeDaTarefa != null && timeDoUsuario != null && timeDaTarefa.getId().equals(timeDoUsuario.getId());
    }

    public boolean temPermissaoParaInteragirComTarefa(UUID id, String nomeTarefa, String username){
        Projeto projeto = this.projetoService.capturarProjetoPorId(id);
        Usuario usuario = this.usuarioService.buscarPorNome(username);
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);
        if (projetoUsuario.getRole().equals(Role.ADMIN)){
            return true;
        }
        return this.tarefaEUsuarioEstaoNoMesmoTime(id, nomeTarefa, username);
    }

    @Transactional
    public Tarefa desvincularTarefaDoUsuario(UUID id, String nomeTarefa, String username){
        Usuario usuario = this.usuarioService.buscarPorNome(username);
        Tarefa tarefa = this.buscarTarefa(id, nomeTarefa);
        boolean existeTarefaUsuario = this.tarefaUsuarioService.existeTarefaUsuario(tarefa, usuario);
        if (!existeTarefaUsuario){
            throw new ConflitoException("Usuário e tarefa não estão vinculados");
        }
        this.tarefaUsuarioService.desvincularTarefaDoUsuario(tarefa, usuario);
        return tarefa;
    }

    @Transactional
    public void deletar(UUID id, String nomeTarefa){
        Tarefa tarefa = this.buscarTarefa(id, nomeTarefa);
        tarefa.getUsuarioRelacionados().forEach(tu -> this.tarefaUsuarioService.desvincularTarefaDoUsuario(tarefa, tu.getUsuario()));
        this.tarefaRepository.delete(tarefa);
    }
}
