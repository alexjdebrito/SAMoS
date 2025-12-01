package samos.repository;

import samos.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PessoaRepositoryCSV {
    private final String caminhoArquivo;
    private final List<Pessoa> pessoas = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);

    public PessoaRepositoryCSV() {
        this.caminhoArquivo = "data/pessoas.csv";
        criarArquivoSeNaoExistir();
        carregarDoArquivo();
    }

    public PessoaRepositoryCSV(String nomeArquivo) {
        this.caminhoArquivo = "data/" + nomeArquivo;
        criarArquivoSeNaoExistir();
        carregarDoArquivo();
    }

    public void carregarDoArquivo() {
        pessoas.clear();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            criarArquivoSeNaoExistir();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            String header = reader.readLine();
            if (header == null) return;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                Pessoa p = converterLinhaParaPessoa(linha);
                if (p != null) pessoas.add(p);
            }
            long maxId = pessoas.stream().filter(x -> x.getId() != null).mapToLong(Pessoa::getId).max().orElse(0L);
            contadorId.set(maxId);
        } catch (Exception e) {
            System.err.println("Erro ao carregar CSV: " + e.getMessage());
        }
    }

    public void salvarNoArquivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo, false))) {
            writer.println(cabecalho());
            for (Pessoa p : pessoas) writer.println(converterPessoaParaLinha(p));
        } catch (IOException e) {
            System.err.println("Erro ao salvar CSV: " + e.getMessage());
        }
    }

    public Pessoa salvar(Pessoa pessoa) {
        if (pessoa.getId() == null) pessoa.setId(contadorId.incrementAndGet());
        else pessoas.removeIf(p -> p.getId().equals(pessoa.getId()));
        pessoas.add(pessoa);
        return pessoa;
    }

    public List<Pessoa> buscarTodos() {
        return new ArrayList<>(pessoas);
    }

    public Optional<Pessoa> buscarPorCpf(String cpf) {
        return pessoas.stream().filter(p -> p.getCpf() != null && p.getCpf().equals(cpf)).findFirst();
    }

    public Optional<Pessoa> buscarPorId(Long id) {
        return pessoas.stream().filter(p -> p.getId() != null && p.getId().equals(id)).findFirst();
    }

    public void limpar() {
        pessoas.clear();
        contadorId.set(0);
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo, false))) {
            writer.println(cabecalho());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void criarArquivoSeNaoExistir() {
        File arquivo = new File(caminhoArquivo);
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) pasta.mkdirs();
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
        String nome = p.getNome() == null ? "" : p.getNome();
        String cpf = p.getCpf() == null ? "" : p.getCpf();
        String data = p.getDataNascimento() != null ? p.getDataNascimento().toString() : "";
        String telefone = p.getTelefone() == null ? "" : p.getTelefone();
        String email = p.getEmail() == null ? "" : p.getEmail();
        if (p instanceof Paciente) {
            Paciente pac = (Paciente) p;
            String tipo = pac.getTipo() != null ? pac.getTipo().name() : "";
            return String.join(";", id, "PACIENTE", nome, cpf, data, telefone, email, tipo, "");
        }
        if (p instanceof Supervisor || p instanceof Funcionario) {
            Funcionario f = (Funcionario) p;
            String cargo = f.getCargo() == null ? "" : f.getCargo();
            String reg = f.getRegistroConselho() == null ? "" : f.getRegistroConselho();
            return String.join(";", id, "FUNCIONARIO", nome, cpf, data, telefone, email, cargo, reg);
        }
        return String.join(";", id, "DESCONHECIDO", nome, cpf, data, telefone, email, "", "");
    }

    private Pessoa converterLinhaParaPessoa(String linha) {
        try {
            String[] c = linha.split(";", -1);
            if (c.length < 7) {
                System.err.println("Linha com formato inválido: " + linha);
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

                // decide se é supervisor/estagiario por cargo
                if ("Supervisor".equalsIgnoreCase(cargo))
                    return new Supervisor(id, nome, cpf, data, telefone, email, reg);
                if ("Estagiário".equalsIgnoreCase(cargo) || "Estagiario".equalsIgnoreCase(cargo))
                    return new Estagiario(id, nome, cpf, data, telefone, email, reg);
                if ("Gestor".equalsIgnoreCase(cargo) || cargo != null && cargo.toLowerCase().contains("gestor"))
                    return new Gestor(id, nome, cpf, data, telefone, email, reg);
                return new Funcionario(id, nome, cpf, data, telefone, email, reg, cargo);
            }
            // Se chegou aqui, é um tipo FUNCIONARIO sem cargo específico.
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao converter linha para Pessoa: " + e.getMessage() + " -> " + linha);
            return null;
        }
    }
}