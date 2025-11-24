package samos.service;

import samos.model.Pessoa;
import samos.repository.PessoaRepositoryCSV;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas ao cadastro
 * e atualização de Pessoas no sistema SAMoS.
 */
public class CadastroService {

    private final PessoaRepositoryCSV repository;

    // Injeção de dependência por construtor
    public CadastroService(PessoaRepositoryCSV repository) {
        this.repository = repository;
    }

    /**
     * Regra de Negócio 1:
     * Cadastra uma nova pessoa (Paciente ou Funcionário).
     *
     * @param pessoa A instância de Pessoa a ser cadastrada.
     * @return A pessoa salva com o ID preenchido.
     * @throws IllegalArgumentException Se o CPF já estiver em uso.
     */
    public Pessoa cadastrarNovaPessoa(Pessoa pessoa) {

        // Regra de Negócio: Garantir que o CPF é único no sistema
        if (repository.buscarPorCpf(pessoa.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Erro: CPF já cadastrado no sistema.");
        }

        // Outras validações podem ser adicionadas aqui (ex: idade, tipo)
        return repository.salvar(pessoa);
    }

    /**
     * Regra de Negócio 2:
     * Atualiza o cadastro de uma pessoa existente.
     *
     * @param pessoa Instância contendo os dados atualizados.
     * @return Pessoa atualizada.
     * @throws IllegalArgumentException Se o ID for inválido ou CPF pertencer a outra pessoa.
     */
    public Pessoa atualizarPessoa(Pessoa pessoa) {

        if (pessoa.getId() == null) {
            throw new IllegalArgumentException("ID da pessoa não pode ser nulo para atualização.");
        }

        // Verificação de manutenção da unicidade do CPF
        repository.buscarPorCpf(pessoa.getCpf()).ifPresent(p -> {
            if (!p.getId().equals(pessoa.getId())) {
                throw new IllegalArgumentException("Erro: O CPF informado já está registrado para outro usuário.");
            }
        });

        return repository.salvar(pessoa);
    }

    /**
     * Busca uma pessoa pelo ID.
     *
     * @param id Identificador único.
     * @return Pessoa encontrada.
     * @throws RuntimeException Se o ID for inexistente.
     */
    public Pessoa buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada para o ID: " + id));
    }

    /**
     * Lista todas as pessoas cadastradas no sistema.
     */
    public List<Pessoa> listarTodos() {
        return repository.buscarTodos();
    }
}
