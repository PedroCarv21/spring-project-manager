package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.ProjetoUsuarioId;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.ProjetoUsuarioRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ProjetoUsuarioRepository projetoUsuarioRepository;
    private final FornecedorUsuarioAutenticado fornecedorUsuarioAutenticado;
    private final ProjetoUsuarioService projetoUsuarioService;

    public Projeto salvar(String nome){
        Usuario usuarioAutenticado = fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<ProjetoUsuario> projetoUsuarioList = this.projetoUsuarioService.listarPorUsuario();
        boolean projetoComEsteNomeJaExiste = projetoUsuarioList
                .stream()
                .anyMatch(projetoUsuario -> projetoUsuario.getProjeto().getNome().equals(nome) && projetoUsuario.getRole().equals(Role.ADMIN));
        if (projetoComEsteNomeJaExiste){
            throw new ConflitoException("Voce ja criou um projeto chamado " + nome);
        }
        Projeto projeto = new Projeto(nome, StatusProjeto.INICIADO);
        Projeto projetoSalvo = this.projetoRepository.save(projeto);
        ProjetoUsuario projetoUsuario = new ProjetoUsuario(new ProjetoUsuarioId(), projetoSalvo, usuarioAutenticado, Role.ADMIN);
        this.projetoUsuarioRepository.save(projetoUsuario);
        return projetoSalvo;
    }

    public Projeto atualizar(String nomeAtual, String novoNome, StatusProjeto statusProjeto){

        Projeto projetoCapturado = this.capturarProjetoAdministradoPorVoce(nomeAtual).get();

        if (novoNome != null && !novoNome.strip().equals("")) {
            List<ProjetoUsuario> projetoUsuarioList = this.projetoUsuarioService.listarPorUsuario();
            boolean existeProjetoComEsteNome = projetoUsuarioList
                    .stream()
                    .anyMatch(projetoUsuario -> projetoUsuario.getProjeto().getNome().equals(novoNome) && !nomeAtual.equals(novoNome) && projetoUsuario.getRole().equals(Role.ADMIN));
            if (existeProjetoComEsteNome) {
                throw new ConflitoException("Já existe um projeto com o nome " + novoNome + ".");
            }
            projetoCapturado.setNome(novoNome);
        }
        if (statusProjeto != null){
            projetoCapturado.setStatus(statusProjeto);
        }
        return this.projetoRepository.save(projetoCapturado);
    }

    public Optional<Projeto> capturarProjetoAdministradoPorVoce(String nome){
        List<ProjetoUsuario> projetoUsuarioList = this.projetoUsuarioService.listarPorUsuario();
        return projetoUsuarioList
                .stream()
                .filter(projetoUsuario -> projetoUsuario.getProjeto().getNome().equals(nome) && projetoUsuario.getRole().equals(Role.ADMIN))
                .map(ProjetoUsuario::getProjeto)
                .findFirst();
    }

    public Projeto capturarProjetoPorId(UUID id){
        return this.projetoRepository
                .findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Projeto não encontrado"));
    }

    public boolean possuiAutorizacaoParaAtualizar(String nomeAtual){
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado();

        boolean existeProjetoComEsteNome = this.existeProjetoComEsteNome(projetos, nomeAtual);
        if (!existeProjetoComEsteNome){
            throw new NaoEncontradoException("Não existe projeto com este nome");
        }

        Optional<Projeto> projetoOptional = this.capturarProjetoAdministradoPorVoce(nomeAtual);
        return projetoOptional.isPresent();
    }

    public boolean possuiAutorizacaoParaSolicitar(UUID id){
        Projeto projeto = this.capturarProjetoPorId(id);
        Usuario usuario = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        ProjetoUsuario projetoUsuario = this.projetoUsuarioService.buscarProjetoUsuario(projeto, usuario);
        return projetoUsuario.getRole().equals(Role.ADMIN) || projetoUsuario.getRole().equals(Role.MANAGER);
    }

    protected boolean existeProjetoComEsteNome(List<Projeto> projetos,  String nome){
        return projetos
                .stream()
                .anyMatch(projeto -> projeto.getNome().equals(nome));
    }

    public List<Projeto> pesquisar(String nome, StatusProjeto statusProjeto){

        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado();

        Stream<Projeto> projetoStream = projetos.stream();

        if (nome != null){
            projetoStream = projetoStream.filter(projeto -> projeto.getNome().equals(nome));
        }
        if (statusProjeto != null){
            projetoStream = projetoStream.filter(projeto -> projeto.getStatus().equals(statusProjeto));
        }

        List<Projeto> projetoList = projetoStream.toList();
        if (projetoList.isEmpty()){
            throw new NaoEncontradoException("Nenhum projeto foi encontrado.");
        }
        return projetoList;
    }

    public void deletar(String nome){

        Projeto projetoCapturado = this.capturarProjetoAdministradoPorVoce(nome).get();

        this.verificarStatusDoProjeto(projetoCapturado);

        projetoCapturado.setStatus(StatusProjeto.CANCELADO);
        this.projetoRepository.save(projetoCapturado);
    }

    protected Projeto buscarPorNome(String nome){
        Optional<Projeto> projetoOptional = this.projetoRepository.findByNome(nome);
        if (projetoOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um projeto com o nome " + nome);
        }
        return projetoOptional.get();
    }

    public void verificarStatusDoProjeto(Projeto projeto){
        if (projeto.getStatus().equals(StatusProjeto.CANCELADO)){
            throw new ConflitoException("Não é possível realizar essa ação com o projeto cancelado");
        }
    }
}
