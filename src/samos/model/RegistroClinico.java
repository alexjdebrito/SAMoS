package samos.model;

import java.time.LocalDateTime;

public class RegistroClinico {
    private Long id;
    private Atendimento atendimento;
    private String diagnostico;
    private String procedimentosRealizados;
    private String evolucao;
    private LocalDateTime dataRegistro;
    private String observacoesGerais;
    private boolean precisaAprovacao;
    private boolean aprovadoPeloSupervisor;
    private Supervisor supervisorAprovador;
    private String feedbackSupervisor;
    public RegistroClinico() {}
    public RegistroClinico(Long id, Atendimento atendimento, String diagnostico, String procedimentosRealizados, String evolucao) {
        this.id = id; this.atendimento = atendimento; this.diagnostico = diagnostico; this.procedimentosRealizados = procedimentosRealizados; this.evolucao = evolucao; this.dataRegistro = LocalDateTime.now(); this.observacoesGerais = "";
        this.precisaAprovacao = atendimento.getProfissional() instanceof Estagiario;
        this.aprovadoPeloSupervisor = !this.precisaAprovacao;
    }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Atendimento getAtendimento() { return atendimento; } public String getDiagnostico() { return diagnostico; }
    public String getProcedimentosRealizados() { return procedimentosRealizados; } public String getEvolucao() { return evolucao; }
    public LocalDateTime getDataRegistro() { return dataRegistro; } public String getObservacoesGerais() { return observacoesGerais; }
    public void setObservacoesGerais(String observacoesGerais) { this.observacoesGerais = observacoesGerais; }
    public boolean isPrecisaAprovacao() { return precisaAprovacao; } public boolean isAprovadoPeloSupervisor() { return aprovadoPeloSupervisor; }
    public void setAprovadoPeloSupervisor(boolean aprovadoPeloSupervisor) { this.aprovadoPeloSupervisor = aprovadoPeloSupervisor; }
    public Supervisor getSupervisorAprovador() { return supervisorAprovador; } public void setSupervisorAprovador(Supervisor supervisorAprovador) { this.supervisorAprovador = supervisorAprovador; }
    public String getFeedbackSupervisor() { return feedbackSupervisor; } public void setFeedbackSupervisor(String feedbackSupervisor) { this.feedbackSupervisor = feedbackSupervisor; }
    @Override public String toString() {
        String statusAprovacao = precisaAprovacao ? (aprovadoPeloSupervisor ? "Aprovado" : "Pendente de Aprovação") : "Não Requer Aprovação";
        return String.format("Registro Clínico [ID: %d, Paciente: %s, Profissional: %s, Data: %s, Status: %s]", id, atendimento.getPaciente().getNome(), atendimento.getProfissional().getNome(), dataRegistro.toLocalDate(), statusAprovacao);
    }
}
