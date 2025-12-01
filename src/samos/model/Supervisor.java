package samos.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Supervisor extends Funcionario {
    private final List<Estagiario> estagiariosSupervisionados;
    public Supervisor() { this.estagiariosSupervisionados = new ArrayList<>(); }
    public Supervisor(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String registroConselho) {
        super(id,nome,cpf,dataNascimento,telefone,email,registroConselho,"Supervisor");
        this.estagiariosSupervisionados = new ArrayList<>();
    }
    public java.util.List<Estagiario> getEstagiariosSupervisionados() { return estagiariosSupervisionados; }
    public void adicionarEstagiario(Estagiario estagiario) { if (!estagiariosSupervisionados.contains(estagiario)) estagiariosSupervisionados.add(estagiario); }
    public void removerEstagiario(Estagiario estagiario) { estagiariosSupervisionados.remove(estagiario); }
    @Override public String toString() { return super.toString() + " | Estagiários Supervisionados: " + estagiariosSupervisionados.size(); }
    @Override public PermissaoAcesso getPermissao() { return PermissaoAcesso.PROFISSIONAL; }
}
