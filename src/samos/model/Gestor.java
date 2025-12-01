package samos.model;

import java.time.LocalDate;

public class Gestor extends Funcionario {
    public Gestor() {}
    public Gestor(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String matricula) {
        super(id,nome,cpf,dataNascimento,telefone,email,matricula,"Gestor Administrativo");
    }
    @Override public PermissaoAcesso getPermissao() { return PermissaoAcesso.GESTOR; }
}
