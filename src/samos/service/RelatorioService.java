package samos.service;

import samos.model.*;
import samos.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {
    private final PessoaRepositoryCSV pessoaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final RegistroClinicoRepository registroRepository;
    public RelatorioService(PessoaRepositoryCSV pessoaRepository, AtendimentoRepository atendimentoRepository, RegistroClinicoRepository registroRepository){
        this.pessoaRepository = pessoaRepository; this.atendimentoRepository = atendimentoRepository; this.registroRepository = registroRepository;
    }
    public java.util.Map<samos.model.Paciente.Tipo, Long> relatorioDistribuicaoPacientes(){
        return pessoaRepository.buscarTodos().stream().filter(p->p instanceof Paciente).map(p->(Paciente)p).collect(Collectors.groupingBy(Paciente::getTipo, Collectors.counting()));
    }
    public java.util.Map<String, Long> relatorioDesempenhoEstagiarios(){
        java.util.Map<String, Long> totalAtendimentos = registroRepository.buscarTodos().stream().filter(r->r.getAtendimento().getProfissional() instanceof Estagiario).collect(Collectors.groupingBy(r->r.getAtendimento().getProfissional().getNome(), Collectors.counting()));
        java.util.Map<String, Long> pendentes = registroRepository.buscarTodos().stream().filter(r->r.isPrecisaAprovacao() && !r.isAprovadoPeloSupervisor()).collect(Collectors.groupingBy(r->r.getAtendimento().getProfissional().getNome(), Collectors.counting()));
        return totalAtendimentos.entrySet().stream().collect(Collectors.toMap(java.util.Map.Entry::getKey, e-> e.getValue() - pendentes.getOrDefault(e.getKey(),0L)));
    }
    public long relatorioTotalAtendimentosNoMes(LocalDate data){
        return atendimentoRepository.buscarTodos().stream().filter(a->a.getDataHora().getMonth()==data.getMonth() && a.getDataHora().getYear()==data.getYear() && a.getStatus().equals("FINALIZADO")).count();
    }
}
