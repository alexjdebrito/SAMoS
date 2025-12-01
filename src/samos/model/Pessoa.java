package samos.model;

import java.time.LocalDate;

public abstract class Pessoa {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;

    public Pessoa() {}

    public Pessoa(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public java.time.LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(java.time.LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public abstract String getTipoPessoa();
    public abstract PermissaoAcesso getPermissao();

    public String baseToCSV() {
        return (id==null?"":id) + ";" + (getTipoPessoa()==null?"":getTipoPessoa()) + ";" + (nome==null?"":nome) + ";" + (cpf==null?"":cpf)
                + ";" + (dataNascimento==null?"":dataNascimento.toString()) + ";" + (telefone==null?"":telefone) + ";" + (email==null?"":email);
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nome + ", CPF: " + cpf + ", Email: " + email;
    }
}
