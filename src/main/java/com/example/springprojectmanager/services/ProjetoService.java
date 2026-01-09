package com.example.springprojectmanager.services;

import com.example.springprojectmanager.enums.StatusTime;
import com.example.springprojectmanager.exceptions.ConflitoException;
import com.example.springprojectmanager.exceptions.NaoEncontradoException;
import com.example.springprojectmanager.entities.Projeto;
import com.example.springprojectmanager.enums.StatusProjeto;
import com.example.springprojectmanager.repositories.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;

    public Projeto salvar(Projeto projeto){
        Projeto projetoSalvo = this.projetoRepository.save(projeto);
        return projetoSalvo;
    }

    public Projeto atualizar(Projeto projetoAtualizado){
        Projeto projetoRegistrado = buscarPorId(projetoAtualizado.getId());
        projetoRegistrado.setNome(projetoAtualizado.getNome());
        projetoRegistrado.setStatus(projetoAtualizado.getStatus());

        if (projetoRegistrado.getStatus().equals(StatusProjeto.CONCLUIDO)){
            atualizarStatusDosTimes(projetoRegistrado, StatusTime.ENCERRADO);
        }
        else{
            atualizarStatusDosTimes(projetoRegistrado, StatusTime.ATIVO);
        }

        return salvar(projetoRegistrado);
    }

    public Page<Projeto> pesquisar(Integer pagina, Integer tamanhoPagina, String nome, StatusProjeto statusProjeto){

        Specification<Projeto> specs = (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        Specification<Projeto> filtro;
        if (nome != null){
            filtro = (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
            specs = specs.and(filtro);
        }
        if (statusProjeto != null){
            filtro = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), statusProjeto);
            specs = specs.and(filtro);
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return this.projetoRepository.findAll(specs, pageRequest);
    }

    public void deletar(String nome){
        Projeto projeto = this.buscarPorNome(nome);
        if (projeto.getStatus().equals(StatusProjeto.CANCELADO)){
            throw new ConflitoException("Esse projeto já foi cancelado.");
        }
        projeto.setStatus(StatusProjeto.CANCELADO);
        atualizarStatusDosTimes(projeto, StatusTime.ENCERRADO);
        this.projetoRepository.save(projeto);
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

    protected Projeto buscarPorId(UUID id){
        Optional<Projeto> projetoOptional = this.projetoRepository.findById(id);
        if (projetoOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um projeto com este ID");
        }
        return projetoOptional.get();
    }
}
