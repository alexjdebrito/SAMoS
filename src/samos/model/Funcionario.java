package samos.model;

import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private String registroConselho;
    private String cargo;
    public Funcionario() {}
    public Funcionario(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String registroConselho, String cargo) {
        super(id,nome,cpf,dataNascimento,telefone,email);
        this.registroConselho = registroConselho;
        this.cargo = cargo;
    }
    public String getRegistroConselho() { return registroConselho; }
    public void setRegistroConselho(String registroConselho) { this.registroConselho = registroConselho; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    @Override public String getTipoPessoa() { return cargo; }
    @Override public PermissaoAcesso getPermissao() { return PermissaoAcesso.PROFISSIONAL; }
    @Override public String toString() { return "Funcionário [" + super.toString() + ", Cargo: " + cargo + ", Reg. Conselho: " + registroConselho + "]"; }
}
