package com.example.springprojectmanager.services;

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
        Optional<Projeto> projetoOptional = buscarPorId(projetoAtualizado.getId());
        Projeto projetoRegistrado = projetoOptional.get();
        projetoRegistrado.setNome(projetoAtualizado.getNome());
        projetoRegistrado.setStatusProjeto(projetoAtualizado.getStatusProjeto());
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
            filtro = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("statusProjeto"), statusProjeto);
            specs = specs.and(filtro);
        }

        PageRequest pageRequest = PageRequest.of(pagina, tamanhoPagina);
        return this.projetoRepository.findAll(specs, pageRequest);
    }

    public void deletar(UUID id){
        Optional<Projeto> projetoOptional = this.buscarPorId(id);
        Projeto projeto = projetoOptional.get();
        projeto.setStatusProjeto(StatusProjeto.CANCELADO);
        this.projetoRepository.save(projeto);
    }

    private Optional<Projeto> buscarPorId(UUID id){
        Optional<Projeto> projetoOptional = this.projetoRepository.findById(id);
        if (projetoOptional.isEmpty()){
            throw new NaoEncontradoException("Nao foi encontrado um projeto com este ID");
        }
        return projetoOptional;
    }
}
