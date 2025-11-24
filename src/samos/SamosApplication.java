package samos;

import samos.model.Funcionario;
import samos.model.Paciente;
import samos.model.Pessoa;
import samos.model.Paciente.Tipo;
import samos.repository.PessoaRepositoryCSV;
import samos.service.CadastroService;

import java.time.LocalDate;
import java.util.List;

public class SamosApplication {

    public static void main(String[] args) {
        System.out.println("🚀 SAMoS - Sistema de Agendamento e Monitoramento de Saúde\n");

        try {

            // 1. Inicialização das Camadas
            System.out.println("--- 1. Inicializando Camadas e Repositório ---");

            // Agora passando o nome do CSV para persistência
            PessoaRepositoryCSV pessoaRepository = new PessoaRepositoryCSV("pessoas.csv");

            // Carrega os dados existentes do CSV (se houver)
            pessoaRepository.carregarDoArquivo();

            CadastroService cadastroService = new CadastroService(pessoaRepository);

            // 2. Cadastro de Pessoas (Pacientes e Funcionários)
            System.out.println("\n--- 2. Cadastro Inicial de Pacientes e Funcionários ---");

            // Pacientes
            Paciente paciente1 = new Paciente(null, "Ana Silva", "111.111.111-11",
                    LocalDate.of(1990, 5, 10), "99991111", "ana@externa.com", Tipo.EXTERNO);

            Paciente paciente2 = new Paciente(null, "Bruno Santos", "222.222.222-22",
                    LocalDate.of(2000, 1, 20), "99992222", "bruno@faculdade.com", Tipo.INTERNO);

            // Funcionários
            Funcionario supervisor = new Funcionario(null, "Dr. Carlos Lima", "333.333.333-33",
                    LocalDate.of(1975, 8, 5), "99993333", "carlos.lima@clinica.com",
                    "CRM/SP 123456", "Supervisor");

            Funcionario estagiario = new Funcionario(null, "Diana Souza", "444.444.444-44",
                    LocalDate.of(2002, 11, 30), "99994444", "diana.souza@estagio.com",
                    "RA 12345", "Estagiário");

            // Realiza os cadastros
            cadastroService.cadastrarNovaPessoa(paciente1);
            cadastroService.cadastrarNovaPessoa(paciente2);
            cadastroService.cadastrarNovaPessoa(supervisor);
            cadastroService.cadastrarNovaPessoa(estagiario);

            System.out.println("✅ Cadastros realizados com sucesso!");


            // 3. Teste da regra de negócio (CPF duplicado)
            System.out.println("\n--- 3. Teste de Regra de Negócio (CPF Duplicado) ---");

            Paciente pacienteDuplicado = new Paciente(null, "Tentativa Duplicada", "111.111.111-11",
                    LocalDate.now(), "00000000", "duplicado@teste.com", Tipo.EXTERNO);

            try {
                cadastroService.cadastrarNovaPessoa(pacienteDuplicado);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Regra de Negócio Aplicada: " + e.getMessage());
            }


            // 4. Listagem geral (polimorfismo)
            System.out.println("\n--- 4. Polimorfismo e Listagem de Todos ---");
            List<Pessoa> todasPessoas = cadastroService.listarTodos();

            for (Pessoa p : todasPessoas) {
                System.out.println(p.getTipoPessoa() + " -> " + p);
            }


            // 5. Atualização de dados (UPDATE)
            System.out.println("\n--- 5. Atualização de Dados ---");

            supervisor.setTelefone("98888-7777");
            cadastroService.atualizarPessoa(supervisor);

            Pessoa supervisorAtualizado = cadastroService.buscarPorId(supervisor.getId());
            System.out.println("Supervisor atualizado: " + supervisorAtualizado);


            // 6. Salvar o CSV ao final
            pessoaRepository.salvarNoArquivo();

            System.out.println("\n💾 Dados salvos no arquivo CSV com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro geral na simulação: " + e.getMessage());
        }

        System.out.println("\nSimulação concluída.");
        System.out.println("------------------------------------------");
    }
}
