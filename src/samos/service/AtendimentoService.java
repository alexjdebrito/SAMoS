package samos.service;

import samos.model.*;
import samos.repository.AtendimentoRepository;
import samos.repository.RegistroClinicoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AtendimentoService {
    private final AtendimentoRepository repository;
    private final RegistroClinicoRepository registroRepository;
    public AtendimentoService(AtendimentoRepository repository, RegistroClinicoRepository registroRepository){
        this.repository = repository;
        this.registroRepository = registroRepository;
    }
    public Atendimento agendarAtendimento(Paciente paciente, Funcionario profissional, LocalDateTime dataHora, String tipoServico, Sala sala){
        // verifica conflito profissional
        List<Atendimento> conflitos = repository.buscarPorProfissionalEDataHora(profissional, dataHora);
        if(!conflitos.isEmpty()) throw new IllegalArgumentException("Erro de Agendamento: O profissional já possui agendamento neste horário.");
        // verifica conflito sala
        List<Atendimento> conflitosSala = repository.buscarPorSalaEDataHora(sala, dataHora);
        if(!conflitosSala.isEmpty()) throw new IllegalArgumentException("Erro de Agendamento: A sala já está reservada para o horário.");
        // data futura
        if(dataHora.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Erro de Agendamento: Não é possível agendar para o passado.");
        if(!sala.isReservavel()) throw new IllegalArgumentException("Erro de Agendamento: A sala está em manutenção e não pode ser reservada.");
        Atendimento novo = new Atendimento(null,paciente,profissional,dataHora,tipoServico,sala);
        return repository.salvar(novo);
    }
    public Atendimento cancelarAtendimento(Long idAtendimento){
        Atendimento atendimento = repository.buscarPorId(idAtendimento).orElseThrow(()->new RuntimeException("Atendimento não encontrado para cancelamento." ));
        if(atendimento.getStatus().equals("CANCELADO") || atendimento.getStatus().equals("REALIZADO")) throw new IllegalStateException("O atendimento já foi " + atendimento.getStatus());
        atendimento.setStatus("CANCELADO");
        return repository.salvar(atendimento);
    }
    public java.util.List<Atendimento> listarTodos(){ return repository.buscarTodos(); }
    public RegistroClinico registrarAtendimento(Long idAtendimento, String diagnostico, String procedimentos, String evolucao){
        Atendimento atendimento = repository.buscarPorId(idAtendimento).orElseThrow(()->new RuntimeException("Atendimento não encontrado para registro."));
        if(!"AGENDADO".equals(atendimento.getStatus())) throw new IllegalStateException("O atendimento não está no status 'AGENDADO' para ser registrado.");
        RegistroClinico registro = new RegistroClinico(null, atendimento, diagnostico, procedimentos, evolucao);
        if(registro.isPrecisaAprovacao()) atendimento.setStatus("EM_AVALIACAO"); else atendimento.setStatus("FINALIZADO");
        repository.salvar(atendimento);
        return registroRepository.salvar(registro);
    }
    public java.util.List<RegistroClinico> buscarHistoricoClinico(Paciente paciente){
        return registroRepository.buscarTodos().stream().filter(r->r.getAtendimento().getPaciente().getId().equals(paciente.getId())).sorted((r1,r2)->r1.getDataRegistro().compareTo(r2.getDataRegistro())).collect(java.util.stream.Collectors.toList());
    }
}
