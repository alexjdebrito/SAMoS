package samos.model;

import java.time.LocalDate;

public class Paciente extends Pessoa {
    public enum Tipo { INTERNO, EXTERNO }
    private Tipo tipo;
    public Paciente() {}
    public Paciente(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, Tipo tipo) {
        super(id,nome,cpf,dataNascimento,telefone,email);
        this.tipo = tipo;
    }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    @Override public String getTipoPessoa() { return "PACIENTE"; }
    @Override public PermissaoAcesso getPermissao() { return PermissaoAcesso.PACIENTE; }
    @Override public String toString() { return "Paciente [" + super.toString() + ", Tipo: " + (tipo==null?"":tipo.name()) + "]"; }
}
