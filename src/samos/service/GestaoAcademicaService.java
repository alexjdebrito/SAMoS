package samos.service;

import samos.model.*;
import samos.repository.RegistroClinicoRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GestaoAcademicaService {
    private final RegistroClinicoRepository registroRepository;
    public GestaoAcademicaService(RegistroClinicoRepository registroRepository){ this.registroRepository = registroRepository; }
    public RegistroClinico avaliarRegistro(Long idRegistro, Supervisor supervisor, boolean aprovado, String feedback){
        RegistroClinico registro = registroRepository.buscarPorId(idRegistro).orElseThrow(()->new RuntimeException("Registro Clínico não encontrado."));
        if(!registro.isPrecisaAprovacao()) throw new IllegalStateException("Este registro não requer aprovação de Supervisor.");
        registro.setAprovadoPeloSupervisor(aprovado); registro.setSupervisorAprovador(supervisor); registro.setFeedbackSupervisor(feedback);
        if(aprovado) registro.getAtendimento().setStatus("FINALIZADO"); else registro.getAtendimento().setStatus("REJEITADO");
        return registroRepository.salvar(registro);
    }
    public List<RegistroClinico> listarPendentesDeAprovacao(){ return registroRepository.buscarTodos().stream().filter(r->r.isPrecisaAprovacao() && !r.isAprovadoPeloSupervisor()).collect(Collectors.toList()); }
}
