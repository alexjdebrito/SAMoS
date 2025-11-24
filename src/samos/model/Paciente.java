package samos.model;

import java.time.LocalDate;

public class Paciente extends Pessoa { // Herança: Paciente É UMA Pessoa

    // Enum para garantir tipos de pacientes válidos
    public enum Tipo { INTERNO, EXTERNO }
    private Tipo tipo; // Exemplo de um atributo que diferencia

    public Paciente(Long id, String nome, String cpf, LocalDate dataNascimento, String telefone, String email, Tipo tipo) {
        super(id, nome, cpf, dataNascimento, telefone, email);
        this.tipo = tipo;
    }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    // Polimorfismo: Implementação do método abstrato
    @Override
    public String getTipoPessoa() {
        return "Paciente (" + tipo.name() + ")";
    }

    @Override
    public String toString() {
        return "Paciente [" + super.toString() + ", Tipo: " + tipo.name() + "]";
    }
}