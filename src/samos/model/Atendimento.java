package samos.model;

import java.time.LocalDateTime;

public class Atendimento {
    private Long id;
    private Paciente paciente;
    private Funcionario profissional;
    private LocalDateTime dataHora;
    private String tipoServico;
    private String status;
    private Sala sala;
    public Atendimento() {}
    public Atendimento(Long id, Paciente paciente, Funcionario profissional, LocalDateTime dataHora, String tipoServico, Sala sala) {
        this.id = id; this.paciente = paciente; this.profissional = profissional; this.dataHora = dataHora; this.tipoServico = tipoServico; this.status = "AGENDADO"; this.sala = sala;
    }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Paciente getPaciente() { return paciente; } public Funcionario getProfissional() { return profissional; }
    public java.time.LocalDateTime getDataHora() { return dataHora; } public String getTipoServico() { return tipoServico; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Sala getSala() { return sala; }
    @Override public String toString() {
        return String.format("Atendimento [ID: %d, Data/Hora: %s, Paciente: %s, Sala: %s, Status: %s]", id, dataHora.toString(), paciente.getNome(), sala.getNome(), status);
    }
}
