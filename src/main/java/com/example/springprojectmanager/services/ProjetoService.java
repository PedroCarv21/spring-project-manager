package com.example.springprojectmanager.services;

import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.entities.ProjetoUsuario;
import com.example.springprojectmanager.entities.Usuario;
import com.example.springprojectmanager.entities.chavesprimariascompostas.ProjetoUsuarioId;
import com.example.springprojectmanager.enums.Role;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import com.example.springprojectmanager.repositories.ProjetoUsuarioRepository;
import com.example.springprojectmanager.security.FornecedorUsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);
        boolean projetoComNomeJaExistente = projetos.stream().anyMatch(projeto -> projeto.getNome().equals(nome));
        if (projetoComNomeJaExistente){
            throw new ConflitoException("Voce ja criou um projeto chamado " + nome);
        }
        Projeto projeto = new Projeto(nome, StatusProjeto.INICIADO);
        Projeto projetoSalvo = this.projetoRepository.save(projeto);
        ProjetoUsuario projetoUsuario = new ProjetoUsuario(new ProjetoUsuarioId(), projetoSalvo, usuarioAutenticado, Role.ADMIN);
        this.projetoUsuarioRepository.save(projetoUsuario);
        return projetoSalvo;
    }

    public Projeto atualizar(String nomeAtual, String novoNome, StatusProjeto statusProjeto){

        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);

        if (novoNome != null) {
            if (this.existeProjetoComEsteNome(projetos, novoNome) && !nomeAtual.equals(novoNome)) {
                throw new ConflitoException("Já existe um projeto com o nome " + novoNome + ".");
            }
        }

        Projeto projetoCapturado = this.capturarProjetoDaLista(projetos, nomeAtual);

        if (novoNome != null){
            projetoCapturado.setNome(novoNome);
        }
        projetoCapturado.setStatus(statusProjeto);
        return this.projetoRepository.save(projetoCapturado);
    }

    public boolean possuiAutorizacaoParaAtualizar(String nomeAtual){
        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<ProjetoUsuario> projetoUsuarioList = this.projetoUsuarioService.listarPorUsuario(usuarioAutenticado);
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);

        boolean existeProjetoComEsteNome = this.existeProjetoComEsteNome(projetos, nomeAtual);
        if (!existeProjetoComEsteNome){
            throw new NaoEncontradoException("Não existe projeto com este nome");
        }

        return projetoUsuarioList
                .stream()
                .anyMatch(projetoUsuario -> projetoUsuario.getProjeto().getNome().equals(nomeAtual) && projetoUsuario.getRole().equals(Role.ADMIN));
    }

    protected boolean existeProjetoComEsteNome(List<Projeto> projetos,  String nome){
        return projetos
                .stream()
                .anyMatch(projeto -> projeto.getNome().equals(nome));
    }

    public List<Projeto> pesquisar(String nome, StatusProjeto statusProjeto){

        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);

        Stream<Projeto> projetoStream = projetos.stream();

        if (nome != null){
            projetoStream = projetoStream.filter(projeto -> projeto.getNome().equals(nome));
        }
        if (statusProjeto != null){
            projetoStream = projetoStream.filter(projeto -> projeto.getStatus().equals(statusProjeto));
        }

        List<Projeto> projetoList = projetoStream.toList();
        if (projetoList.isEmpty()){
            throw new NaoEncontradoException("Não foi encontrado um projeto com estas definições.");
        }
        return projetoList;
    }

    public void deletar(String nome){

        Usuario usuarioAutenticado = this.fornecedorUsuarioAutenticado.fornecerUsuarioAutenticado();
        List<Projeto> projetos = this.projetoUsuarioService.listarProjetosDoUsuarioAutenticado(usuarioAutenticado);

        Projeto projetoCapturado = this.capturarProjetoDaLista(projetos, nome);

        if (projetoCapturado.getStatus().equals(StatusProjeto.CANCELADO)){
            throw new ConflitoException("Esse projeto já foi cancelado.");
        }
        projetoCapturado.setStatus(StatusProjeto.CANCELADO);
//        atualizarStatusDosTimes(projetoCapturado, StatusTime.ENCERRADO);
        this.projetoRepository.save(projetoCapturado);
    }

    protected Projeto capturarProjetoDaLista(List<Projeto> projetos, String nome){
        return projetos
                .stream()
                .filter(projeto -> projeto.getNome().equals(nome))
                .findFirst()
                .get();
    }

    protected void atualizarStatusDosTimes(Projeto projeto, StatusTime statusTime){
        projeto.getTimes().forEach(time -> time.setStatus(statusTime));
    }

    protected Projeto buscarPorNome(String nome){
        Optional<Projeto> projetoOptional = this.projetoRepository.findByNome(nome);
        if (projetoOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um projeto com o nome " + nome);
        }
        return projetoOptional.get();
    }
}
