package samos.model;

import java.time.LocalDate;
import java.util.Objects;

public class Estagiario extends Funcionario {
    private Supervisor supervisor;
    public Estagiario() {}
    public Estagiario(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String registroAcademico) {
        super(id,nome,cpf,dataNascimento,telefone,email,registroAcademico,"Estagiário");
    }
    public Supervisor getSupervisor() { return supervisor; }
    public void setSupervisor(Supervisor supervisor) { this.supervisor = supervisor; }
    @Override public String toString() {
        String infoSupervisor = supervisor != null ? supervisor.getNome() : "Nenhum";
        return super.toString() + " | Supervisor: " + infoSupervisor;
    }
    @Override public PermissaoAcesso getPermissao() { return PermissaoAcesso.ESTAGIARIO; }
}
