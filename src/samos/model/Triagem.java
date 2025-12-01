package samos.model;

public class Triagem {
    public enum NivelPrioridade { BAIXA, MEDIA, ALTA, EMERGENCIA }
    private Paciente paciente;
    private NivelPrioridade prioridade;
    private String motivo;
    public Triagem(Paciente paciente, NivelPrioridade prioridade, String motivo) { this.paciente = paciente; this.prioridade = prioridade; this.motivo = motivo; }
    public Paciente getPaciente() { return paciente; } public NivelPrioridade getPrioridade() { return prioridade; } public String getMotivo() { return motivo; }
    @Override public String toString() { return "Triagem [Paciente: " + paciente.getNome() + ", Prioridade: " + prioridade.name() + ", Motivo: " + motivo + "]"; }
}
