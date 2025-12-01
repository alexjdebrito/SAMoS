package samos.service;

import samos.model.Pessoa;
import samos.repository.PessoaRepositoryCSV;

import java.util.List;

public class CadastroService {
    private final PessoaRepositoryCSV repository;
    public CadastroService(PessoaRepositoryCSV repository){ this.repository = repository; }
    public Pessoa cadastrarNovaPessoa(Pessoa pessoa){
        if(repository.buscarPorCpf(pessoa.getCpf()).isPresent()) throw new IllegalArgumentException("Erro: CPF já cadastrado no sistema.");
        return repository.salvar(pessoa);
    }
    public Pessoa atualizarPessoa(Pessoa pessoa){
        if(pessoa.getId()==null) throw new IllegalArgumentException("ID da pessoa não pode ser nulo para atualização.");
        repository.buscarPorCpf(pessoa.getCpf()).ifPresent(p->{ if(!p.getId().equals(pessoa.getId())) throw new IllegalArgumentException("Erro: O CPF informado já está registrado para outro usuário."); });
        return repository.salvar(pessoa);
    }
    public Pessoa buscarPorId(Long id){ return repository.buscarPorId(id).orElseThrow(()->new RuntimeException("Pessoa não encontrada para o ID: " + id)); }
    public List<Pessoa> listarTodos(){ return repository.buscarTodos(); }
}
