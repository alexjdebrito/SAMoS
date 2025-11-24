package samos.repository;

import samos.model.Pessoa;
import samos.model.Paciente;
import samos.model.Funcionario;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PessoaRepositoryCSV {

    private final String caminhoArquivo;
    private final List<Pessoa> pessoas = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    /**
     * Construtor que recebe o nome do arquivo CSV (ex: "pessoas.csv").
     * O arquivo ficará em data/{nomeArquivo}.
     */
    public PessoaRepositoryCSV(String nomeArquivo) {
        this.caminhoArquivo = "data/" + nomeArquivo;
        criarArquivoSeNaoExistir();
    }

    /**
     * Carrega do CSV para a lista em memória e atualiza o contador de IDs.
     */
    public void carregarDoArquivo() {
        pessoas.clear();

        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            // Arquivo pode ter sido criado no construtor; só garantir
            criarArquivoSeNaoExistir();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            // Pula cabeçalho (se existir)
            String header = reader.readLine();
            if (header == null) return;

            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                Pessoa p = converterLinhaParaPessoa(linha);
                if (p != null) pessoas.add(p);
            }

            long maxId = pessoas.stream()
                    .mapToLong(Pessoa::getId)
                    .max()
                    .orElse(0L);

            contadorId.set(maxId);

        } catch (Exception e) {
            System.err.println("Erro ao carregar CSV: " + e.getMessage());
        }
    }

    /**
     * Salva toda a lista em memória no CSV (reescreve o arquivo).
     */
    public void salvarNoArquivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo, false))) {
            writer.println(cabecalho());
            for (Pessoa p : pessoas) {
                writer.println(converterPessoaParaLinha(p));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar CSV: " + e.getMessage());
        }
    }

    // -----------------------
    // CRUD em memória (usados pelo service)
    // -----------------------

    public Pessoa salvar(Pessoa pessoa) {
        if (pessoa.getId() == null) {
            pessoa.setId(contadorId.incrementAndGet());
        } else {
            pessoas.removeIf(p -> p.getId().equals(pessoa.getId()));
        }
        pessoas.add(pessoa);
        return pessoa;
    }

    public List<Pessoa> buscarTodos() {
        return new ArrayList<>(pessoas);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoas.stream()
                .filter(p -> p.getCpf() != null && p.getCpf().equals(cpf))
                .findFirst();
    }

    public Optional<Pessoa> buscarPorId(Long id) {
        return pessoas.stream()
                .filter(p -> p.getId() != null && p.getId().equals(id))
                .findFirst();
    }

    public void limpar() {
        pessoas.clear();
        contadorId.set(0);
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo, false))) {
            writer.println(cabecalho());
        } catch (IOException e) {
            System.err.println("Erro ao limpar CSV: " + e.getMessage());
        }
    }

    // -----------------------
    // Helpers para CSV
    // -----------------------

    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(caminhoArquivo);
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }
        if (!arquivo.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo, false))) {
                writer.println(cabecalho());
            } catch (IOException e) {
                System.err.println("Erro ao criar CSV: " + e.getMessage());
            }
        }
    }

    private String cabecalho() {
        return "id;tipo;nome;cpf;dataNascimento;telefone;email;extra1;extra2";
    }

    private String converterPessoaParaLinha(Pessoa p) {
        if (p == null) return "";

        String id = p.getId() != null ? p.getId().toString() : "";
        String nome = safe(p.getNome());
        String cpf = safe(p.getCpf());
        String data = p.getDataNascimento() != null ? p.getDataNascimento().toString() : "";
        String telefone = safe(p.getTelefone());
        String email = safe(p.getEmail());

        if (p instanceof Paciente) {
            Paciente pac = (Paciente) p;
            String tipo = pac.getTipo() != null ? pac.getTipo().name() : "";
            return String.join(";", id, "PACIENTE", nome, cpf, data, telefone, email, tipo, "");
        }

        if (p instanceof Funcionario) {
            Funcionario f = (Funcionario) p;
            String cargo = safe(f.getCargo());
            String reg = safe(f.getRegistroConselho());
            return String.join(";", id, "FUNCIONARIO", nome, cpf, data, telefone, email, cargo, reg);
        }

        // Outros tipos (se adicionar futuramente) devem ser tratados aqui
        return String.join(";", id, "DESCONHECIDO", nome, cpf, data, telefone, email, "", "");
    }

    private Pessoa converterLinhaParaPessoa(String linha) {
        try {
            String[] c = linha.split(";", -1); // -1 preserva campos vazios

            if (c.length < 7) {
                System.err.println("Linha com formato inválido (menos campos que o esperado): " + linha);
                return null;
            }

            Long id = c[0].isEmpty() ? null : Long.parseLong(c[0]);
            String tipo = c[1];
            String nome = c[2];
            String cpf = c[3];
            LocalDate data = (c[4] == null || c[4].isEmpty()) ? null : LocalDate.parse(c[4]);
            String telefone = c[5];
            String email = c[6];

            if ("PACIENTE".equalsIgnoreCase(tipo)) {
                Paciente.Tipo pTipo = (c.length > 7 && !c[7].isEmpty()) ? Paciente.Tipo.valueOf(c[7]) : null;
                return new Paciente(id, nome, cpf, data, telefone, email, pTipo);
            }

            if ("FUNCIONARIO".equalsIgnoreCase(tipo)) {
                String cargo = (c.length > 7) ? c[7] : null;
                String reg = (c.length > 8) ? c[8] : null;
                return new Funcionario(id, nome, cpf, data, telefone, email, reg, cargo);
            }

            // Se tipo desconhecido, pode-se estender aqui
        } catch (Exception e) {
            System.err.println("Erro ao converter linha para Pessoa: " + e.getMessage() + " -> linha: " + linha);
        }
        return null;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
