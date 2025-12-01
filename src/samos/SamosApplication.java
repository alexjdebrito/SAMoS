package samos;

import samos.model.*;
import samos.model.Triagem.NivelPrioridade;
import samos.repository.*;
import samos.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors; // Necessário se for usar import estático ou completo

public class SamosApplication {

    private static PessoaRepositoryCSV pessoaRepository;
    private static AtendimentoRepository atendimentoRepository;
    private static RegistroClinicoRepository registroRepository;
    private static SalaRepository salaRepository;
    private static CadastroService cadastroService;
    private static AtendimentoService atendimentoService;
    private static DocumentacaoService documentacaoService;
    private static RelatorioService relatorioService;
    private static TriagemService triagemService;
    private static NotificacaoService notificacaoService;
    private static GestaoAcademicaService avaliacaoService;
    private static GestaoFuncionariosService gestaoFuncionariosService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("🚀 SAMoS - Sistema de Agendamento e Monitoramento de Saúde\n");
        Scanner scanner = new Scanner(System.in);

        inicializarComponentes();

        executarSimulacaoInicial();

        menuPrincipal(scanner);

        pessoaRepository.salvarNoArquivo();
        System.out.println("\n************************************************");
        System.out.println("Dados salvos no arquivo 'pessoas.csv'.");
        System.out.println("PROJETO SAMoS ENCERRADO!");
        System.out.println("************************************************");
        scanner.close();
    }

    private static void inicializarComponentes() {
        pessoaRepository = new PessoaRepositoryCSV();
        atendimentoRepository = new AtendimentoRepository();
        registroRepository = new RegistroClinicoRepository();
        salaRepository = new SalaRepository();
        cadastroService = new CadastroService(pessoaRepository);
        atendimentoService = new AtendimentoService(atendimentoRepository, registroRepository);
        documentacaoService = new DocumentacaoService(registroRepository);
        relatorioService = new RelatorioService(pessoaRepository, atendimentoRepository, registroRepository);
        triagemService = new TriagemService();
        notificacaoService = new NotificacaoService();
        avaliacaoService = new GestaoAcademicaService(registroRepository);
        gestaoFuncionariosService = new GestaoFuncionariosService();
    }

    private static void executarSimulacaoInicial() {
        try {
            System.out.println("--- Carregando/Atualizando dados iniciais da simulação ---\n");

            // Carrega todas as pessoas existentes na memória
            List<Pessoa> pessoasExistentes = pessoaRepository.buscarTodos();

            // --- FUNÇÃO AUXILIAR PARA EVITAR DUPLICAÇÃO POR CPF ---
            java.util.function.Function<Pessoa, Pessoa> buscarOuCriar = (novaPessoa) -> {
                Optional<Pessoa> existente = pessoasExistentes.stream()
                        .filter(p -> p.getCpf().equals(novaPessoa.getCpf()))
                        .findFirst();

                if (existente.isPresent()) {
                    // Se a pessoa existe, usa o ID existente para forçar a ATUALIZAÇÃO
                    novaPessoa.setId(existente.get().getId());
                }
                // Se não existe, o ID continua null e será gerado um novo ID no .salvar()
                return pessoaRepository.salvar(novaPessoa);
            };

            // --- 1. PESSOAS ---
            // Os objetos salvos agora terão o ID do arquivo CSV (se já existirem)
            Pessoa alex = buscarOuCriar.apply(new Paciente(null, "Alex de Brito", "111.111.111-11", LocalDate.of(1983,10,18), "83999999999", "alex@interno.com", Paciente.Tipo.INTERNO));
            Pessoa juliana = buscarOuCriar.apply(new Paciente(null, "Juliana de Melo", "222.222.222-22", LocalDate.of(1997,1,17), "83999999990", "juliana@externa.com", Paciente.Tipo.EXTERNO));
            Pessoa joao = buscarOuCriar.apply(new Supervisor(null, "Dr. João de Melo", "333.333.333-33", LocalDate.of(1981,3,27), "83999999991", "joao.melo@clinica.com", "CRM/PB 123456"));
            Pessoa paloma = buscarOuCriar.apply(new Estagiario(null, "Paloma Santos", "444.444.444-44", LocalDate.of(2000,01,01), "83999999992", "paloma.santos@estagio.com", "RA 12345"));
            Pessoa junior = buscarOuCriar.apply(new Gestor(null, "Junior Amorim", "999.999.999-99", LocalDate.of(1980,02,02), "83999999993", "junior@gerencia.com", "MAT987"));


            // --- 2. SALAS (Busca pela Chave de Negócio: Nome da Sala) ---
            Sala sala = new Sala(null, "Consultório 1 (Psico)", "Bloco C", 5);
            salaRepository.buscarTodos().stream()
                    .filter(s -> s.getNome().equals(sala.getNome()))
                    .findFirst()
                    .ifPresent(s -> sala.setId(s.getId())); // Se encontrar, usa o ID

            salaRepository.salvar(sala); // Salva/Atualiza

            // Re-inicializa o CadastroService (boa prática para garantir o estado)
            cadastroService = new CadastroService(pessoaRepository);

            System.out.println("✅ Dados iniciais carregados/atualizados (" + pessoaRepository.buscarTodos().size() + " Pessoas, 1 Sala).");
        } catch (Exception e) {
            System.err.println("❌ ERRO GRAVE ao carregar dados iniciais: " + e.getMessage());
        }
    }

    private static void menuPrincipal(Scanner scanner) {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=============================================");
            System.out.println("💻 MENU PRINCIPAL SAMoS (Total Pessoas: " + cadastroService.listarTodos().size() + ")");
            System.out.println("=============================================");
            System.out.println("1. Cadastro de Pessoas (Paciente/Funcionário)");
            System.out.println("2. Agendamento e Atendimento");
            System.out.println("3. Triagem e Prioridade");
            System.out.println("4. Relatórios e Gestão");
            System.out.println("0. Sair e Salvar Dados");
            System.out.print(">> Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1: menuCadastro(scanner); break;
                    case 2: menuAtendimento(scanner); break;
                    case 3: menuTriagem(scanner); break;
                    case 4: menuRelatorios(scanner); break;
                    case 0: break;
                    default: System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.err.println("❌ Entrada inválida. Por favor, digite um número.");
                scanner.nextLine(); // Limpa o buffer
            } catch (Exception e) {
                System.err.println("❌ ERRO: " + e.getMessage());
            }
        }
    }

    private static void menuCadastro(Scanner scanner) {
        System.out.println("\n--- MENU CADASTRO ---");
        System.out.println("1. Cadastrar Paciente");
        System.out.println("2. Cadastrar Funcionário (Supervisor/Estagiário/Gestor)");
        System.out.println("3. Listar Todas as Pessoas");
        System.out.print(">> Escolha uma opção: ");

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            if (opcao == 1) {
                cadastrarPaciente(scanner);
            } else if (opcao == 2) {
                cadastrarFuncionario(scanner);
            } else if (opcao == 3) {
                listarTodasPessoas();
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERRO no Cadastro: " + e.getMessage());
        }
    }

    private static void cadastrarPaciente(Scanner scanner) throws Exception {
        System.out.println("\n--- CADASTRO DE PACIENTE ---");
        System.out.print("Nome: "); String nome = scanner.nextLine();
        System.out.print("CPF (formato 000.000.000-00): "); String cpf = scanner.nextLine();
        System.out.print("Data Nasc. (dd/MM/yyyy): "); LocalDate dataNasc = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
        System.out.print("Telefone: "); String tel = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Tipo (EXTERNO/INTERNO): "); Paciente.Tipo tipo = Paciente.Tipo.valueOf(scanner.nextLine().toUpperCase());

        Paciente novo = new Paciente(null, nome, cpf, dataNasc, tel, email, tipo);
        cadastroService.cadastrarNovaPessoa(novo);
        System.out.println("✅ Paciente cadastrado com sucesso! ID: " + novo.getId());
    }

    private static void cadastrarFuncionario(Scanner scanner) throws Exception {
        System.out.println("\n--- CADASTRO DE FUNCIONÁRIO ---");
        System.out.print("Nome: "); String nome = scanner.nextLine();
        System.out.print("CPF: "); String cpf = scanner.nextLine();
        System.out.print("Data Nasc. (dd/MM/yyyy): "); LocalDate dataNasc = LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
        System.out.print("Telefone: "); String tel = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();

        System.out.println("Cargo (1-Supervisor, 2-Estagiário, 3-Gestor): ");
        int cargoOpt = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Registro (CRM/RA/MAT): "); String reg = scanner.nextLine();

        Pessoa novo;
        if (cargoOpt == 1) {
            novo = new Supervisor(null, nome, cpf, dataNasc, tel, email, reg);
        } else if (cargoOpt == 2) {
            novo = new Estagiario(null, nome, cpf, dataNasc, tel, email, reg);
        } else if (cargoOpt == 3) {
            novo = new Gestor(null, nome, cpf, dataNasc, tel, email, reg);
        } else {
            throw new IllegalArgumentException("Opção de cargo inválida.");
        }

        cadastroService.cadastrarNovaPessoa(novo);
        System.out.println("✅ " + novo.getTipoPessoa() + " cadastrado com sucesso! ID: " + novo.getId());
    }

    private static void listarTodasPessoas() {
        System.out.println("\n--- LISTA DE PESSOAS ---");
        List<Pessoa> pessoas = cadastroService.listarTodos();
        if (pessoas.isEmpty()) { System.out.println("Nenhuma pessoa cadastrada."); return; }

        pessoas.forEach(p -> {
            String detalhes = "";
            if (p instanceof Paciente) detalhes = " | Tipo: " + ((Paciente)p).getTipo();
            if (p instanceof Funcionario) detalhes = " | Cargo: " + ((Funcionario)p).getCargo();
            System.out.printf("ID %d | %s (%s) | CPF: %s%s\n", p.getId(), p.getNome(), p.getTipoPessoa(), p.getCpf(), detalhes);
        });
    }

    private static void menuAtendimento(Scanner scanner) {
        System.out.println("\n--- MENU ATENDIMENTO ---");
        System.out.println("1. Agendar Novo Atendimento");
        System.out.println("2. Registrar Atendimento (Fim da Consulta)");
        System.out.println("3. Listar Agendamentos");
        System.out.print(">> Escolha uma opção: ");

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            if (opcao == 1) {
                agendarAtendimento(scanner);
            } else if (opcao == 2) {
                registrarAtendimento(scanner);
            } else if (opcao == 3) {
                listarAtendimentos();
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERRO no Atendimento: " + e.getMessage());
        }
    }

    private static void agendarAtendimento(Scanner scanner) throws Exception {
        System.out.println("\n--- NOVO AGENDAMENTO ---");

        listarPacientes();
        System.out.print("ID do Paciente: "); Long idPaciente = scanner.nextLong();
        scanner.nextLine();
        Paciente paciente = (Paciente) cadastroService.buscarPorId(idPaciente);

        listarProfissionais();
        System.out.print("ID do Profissional: "); Long idProfissional = scanner.nextLong();
        scanner.nextLine();
        Funcionario profissional = (Funcionario) cadastroService.buscarPorId(idProfissional);

        listarSalas();
        System.out.print("ID da Sala: "); Long idSala = scanner.nextLong();
        scanner.nextLine();
        Sala sala = salaRepository.buscarPorId(idSala).orElseThrow(() -> new RuntimeException("Sala não encontrada."));

        System.out.print("Data e Hora (dd/MM/yyyy HH:mm): "); LocalDateTime dataHora = LocalDateTime.parse(scanner.nextLine(), DATETIME_FORMATTER);
        System.out.print("Tipo de Serviço: "); String tipoServico = scanner.nextLine();

        Atendimento novo = atendimentoService.agendarAtendimento(paciente, profissional, dataHora, tipoServico, sala);
        System.out.println("✅ Agendamento realizado com sucesso! " + novo);
        notificacaoService.enviarLembrete(novo); // Envia lembrete após agendar
    }

    private static void registrarAtendimento(Scanner scanner) throws Exception {
        listarAtendimentosPendentes();
        System.out.print("ID do Atendimento a ser REGISTRADO (Finalizado): "); Long idAtendimento = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Diagnóstico: "); String diag = scanner.nextLine();
        System.out.print("Procedimentos Realizados: "); String proc = scanner.nextLine();
        System.out.print("Evolução (OK/Melhora/etc): "); String evol = scanner.nextLine();

        RegistroClinico registro = atendimentoService.registrarAtendimento(idAtendimento, diag, proc, evol);

        String statusFinal = registro.isPrecisaAprovacao() ? "EM AVALIAÇÃO pelo Supervisor." : "FINALIZADO.";
        System.out.println("✅ Registro Clínico salvo! Status do Atendimento: " + statusFinal);

        if (registro.isPrecisaAprovacao()) {
            System.out.println("👉 ATENÇÃO: Este registro é de Estagiário e precisa de aprovação.");
            menuAvaliacao(registro.getId(), scanner);
        }
    }

    private static void menuAvaliacao(Long idRegistro, Scanner scanner) {
        try {
            System.out.print("ID do Supervisor Aprovador: "); Long idSupervisor = scanner.nextLong();
            scanner.nextLine();
            Supervisor supervisor = (Supervisor) cadastroService.buscarPorId(idSupervisor);
            if (!(supervisor instanceof Supervisor)) throw new IllegalArgumentException("O ID informado não pertence a um Supervisor.");

            System.out.print("Resultado da Avaliação (S/N): "); boolean aprovado = scanner.nextLine().toUpperCase().startsWith("S");
            System.out.print("Feedback do Supervisor: "); String feedback = scanner.nextLine();

            RegistroClinico reg = avaliacaoService.avaliarRegistro(idRegistro, supervisor, aprovado, feedback);
            System.out.println("✅ Registro de Estágio avaliado. Status final do Atendimento: " + reg.getAtendimento().getStatus());
        } catch (Exception e) {
            System.err.println("❌ ERRO na Avaliação: " + e.getMessage());
        }
    }

    private static void listarAtendimentos() {
        System.out.println("\n--- LISTA DE ATENDIMENTOS ---");
        List<Atendimento> atendimentos = atendimentoService.listarTodos();
        if (atendimentos.isEmpty()) { System.out.println("Nenhum atendimento agendado/realizado."); return; }

        atendimentos.forEach(a -> System.out.printf("ID %d | %s | Prof: %s | Pac: %s | Sala: %s | Status: %s\n", a.getId(), a.getDataHora().format(DATETIME_FORMATTER), a.getProfissional().getNome(), a.getPaciente().getNome(), a.getSala().getNome(), a.getStatus()));
    }

    private static void listarAtendimentosPendentes() {
        System.out.println("\n--- ATENDIMENTOS AGENDADOS ---");
        List<Atendimento> pendentes = atendimentoService.listarTodos().stream()
                .filter(a -> "AGENDADO".equals(a.getStatus()))
                .collect(java.util.stream.Collectors.toList());
        if (pendentes.isEmpty()) { System.out.println("Nenhum atendimento AGENDADO pendente."); return; }

        pendentes.forEach(a -> System.out.printf("ID %d | %s | Prof: %s | Pac: %s | Sala: %s\n", a.getId(), a.getDataHora().format(DATETIME_FORMATTER), a.getProfissional().getNome(), a.getPaciente().getNome(), a.getSala().getNome()));
    }

    private static void listarPacientes() {
        System.out.println("\n--- LISTA DE PACIENTES ---");
        pessoaRepository.buscarTodos().stream()
                .filter(p -> p instanceof Paciente)
                .forEach(p -> System.out.printf("ID %d | %s | CPF: %s | Tipo: %s\n", p.getId(), p.getNome(), p.getCpf(), ((Paciente)p).getTipo()));
    }

    private static void listarProfissionais() {
        System.out.println("\n--- LISTA DE PROFISSIONAIS ---");
        pessoaRepository.buscarTodos().stream()
                .filter(p -> p instanceof Funcionario && p.getPermissao() != PermissaoAcesso.GESTOR)
                .forEach(p -> System.out.printf("ID %d | %s | Cargo: %s\n", p.getId(), p.getNome(), ((Funcionario)p).getCargo()));
    }

    private static void listarSalas() {
        System.out.println("\n--- LISTA DE SALAS ---");
        salaRepository.buscarTodos().forEach(s -> System.out.printf("ID %d | %s | %s\n", s.getId(), s.getNome(), s.getLocalizacao()));
    }

    private static void menuTriagem(Scanner scanner) {
        System.out.println("\n--- MENU TRIAGEM ---");
        System.out.println("1. Realizar Triagem (Adicionar à Fila)");
        System.out.println("2. Chamar Próximo Paciente (Prioridade Máxima)");
        System.out.println("3. Mostrar Fila Atual");
        System.out.print(">> Escolha uma opção: ");

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                realizarTriagem(scanner);
            } else if (opcao == 2) {
                chamarProximoPaciente();
            } else if (opcao == 3) {
                System.out.println("Fila de Triagem: " + triagemService.getTamanhoFila() + " pacientes.");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERRO na Triagem: " + e.getMessage());
        }
    }

    private static void realizarTriagem(Scanner scanner) throws Exception {
        listarPacientes();
        System.out.print("ID do Paciente: "); Long idPaciente = scanner.nextLong();
        scanner.nextLine();
        Paciente paciente = (Paciente) cadastroService.buscarPorId(idPaciente);

        System.out.print("Motivo da Consulta: "); String motivo = scanner.nextLine();

        System.out.println("Nível de Prioridade (1-BAIXA, 2-MEDIA, 3-ALTA, 4-EMERGENCIA): ");
        int nivelOpt = scanner.nextInt();
        scanner.nextLine();

        NivelPrioridade nivel;
        switch (nivelOpt) {
            case 1: nivel = NivelPrioridade.BAIXA; break;
            case 2: nivel = NivelPrioridade.MEDIA; break;
            case 3: nivel = NivelPrioridade.ALTA; break;
            case 4: nivel = NivelPrioridade.EMERGENCIA; break;
            default: throw new IllegalArgumentException("Nível de prioridade inválido.");
        }

        triagemService.realizarTriagem(paciente, motivo, nivel);
    }

    private static void chamarProximoPaciente() {
        Triagem proximo = triagemService.chamarProximoPaciente();
        if (proximo == null) {
            System.out.println("A fila de triagem está vazia.");
        } else {
            System.out.println("📞 PRÓXIMO A SER CHAMADO (Prioridade Máxima):");
            System.out.println("   Paciente: " + proximo.getPaciente().getNome());
            System.out.println("   Prioridade: " + proximo.getPrioridade().name());
            System.out.println("   Motivo: " + proximo.getMotivo());
        }
    }

    private static void menuRelatorios(Scanner scanner) {
        System.out.println("\n--- MENU RELATÓRIOS/GESTÃO ---");
        System.out.println("1. Relatório de Distribuição de Pacientes");
        System.out.println("2. Relatório de Desempenho de Estagiários");
        System.out.println("3. Total de Atendimentos FINALIZADOS no Mês");
        System.out.println("4. Emitir Atestado de Comparecimento");
        System.out.println("5. Associar Estagiário a Supervisor");
        System.out.print(">> Escolha uma opção: ");

        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                System.out.println("\n--- DISTRIBUIÇÃO DE PACIENTES ---");
                System.out.println(relatorioService.relatorioDistribuicaoPacientes());
            } else if (opcao == 2) {
                System.out.println("\n--- DESEMPENHO DE ESTAGIÁRIOS (Aprovados) ---");
                relatorioService.relatorioDesempenhoEstagiarios().forEach((nome, aprovados) -> System.out.println("Estagiário " + nome + ": " + aprovados + " atendimento(s) aprovado(s)."));
            } else if (opcao == 3) {
                LocalDate hoje = LocalDate.now();
                long totalMes = relatorioService.relatorioTotalAtendimentosNoMes(hoje);
                System.out.println("\nTotal de Atendimentos FINALIZADOS no Mês " + hoje.getMonthValue() + ": " + totalMes);
            } else if (opcao == 4) {
                emitirAtestado(scanner);
            } else if (opcao == 5) {
                associarEstagiarioSupervisor(scanner);
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERRO no Relatório: " + e.getMessage());
        }
    }

    private static void emitirAtestado(Scanner scanner) throws Exception {
        listarAtendimentos();
        System.out.print("ID do Atendimento para emitir atestado: "); Long idAtendimento = scanner.nextLong();
        scanner.nextLine();
        String atestado = documentacaoService.emitirAtestadoComparecimento(idAtendimento);
        System.out.println("\n" + atestado);
    }

    private static void associarEstagiarioSupervisor(Scanner scanner) throws Exception {
        listarProfissionais();
        System.out.print("ID do Estagiário: "); Long idEstagiario = scanner.nextLong();
        scanner.nextLine();
        System.out.print("ID do Supervisor: "); Long idSupervisor = scanner.nextLong();
        scanner.nextLine();

        Pessoa pEstagiario = cadastroService.buscarPorId(idEstagiario);
        Pessoa pSupervisor = cadastroService.buscarPorId(idSupervisor);

        if (!(pEstagiario instanceof Estagiario)) throw new IllegalArgumentException("O primeiro ID não pertence a um Estagiário.");
        if (!(pSupervisor instanceof Supervisor)) throw new IllegalArgumentException("O segundo ID não pertence a um Supervisor.");

        gestaoFuncionariosService.associarEstagiarioSupervisor((Estagiario)pEstagiario, (Supervisor)pSupervisor);
    }
}