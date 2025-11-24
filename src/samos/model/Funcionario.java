package samos.model;

import java.time.LocalDate;

public class Funcionario extends Pessoa { // Herança: Funcionario É UMA Pessoa

    private String registroConselho; // CRM, COREN, etc.
    private String cargo; // Ex: Supervisor, Estagiário

    public Funcionario(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String registroConselho, String cargo) {
        super(id, nome, cpf, dataNascimento, telefone, email);
        this.registroConselho = registroConselho;
        this.cargo = cargo;
    }

    public String getRegistroConselho() { return registroConselho; }
    public void setRegistroConselho(String registroConselho) { this.registroConselho = registroConselho; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String getTipoPessoa() {
        return cargo; // Polimorfismo
    }

    @Override
    public String toString() {
        return "Funcionário [" + super.toString() + ", Cargo: " + cargo + ", Reg. Conselho: " + registroConselho + "]";
    }
}