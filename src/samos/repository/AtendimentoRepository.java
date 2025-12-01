package samos.repository;

import samos.model.Atendimento;
import samos.model.Funcionario;
import samos.model.Sala;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AtendimentoRepository {
    private final List<Atendimento> bancoDeDados = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);
    public Atendimento salvar(Atendimento atendimento){
        if(atendimento.getId()==null) atendimento.setId(contadorId.incrementAndGet());
        else bancoDeDados.removeIf(a->a.getId().equals(atendimento.getId()));
        bancoDeDados.add(atendimento); return atendimento;
    }
    public Optional<Atendimento> buscarPorId(Long id){ return bancoDeDados.stream().filter(a->a.getId().equals(id)).findFirst(); }
    public List<Atendimento> buscarTodos(){ return new ArrayList<>(bancoDeDados); }
    public List<Atendimento> buscarPorProfissionalEDataHora(Funcionario profissional, LocalDateTime dataHora){
        return bancoDeDados.stream().filter(a->a.getProfissional().equals(profissional) && a.getDataHora().equals(dataHora) && a.getStatus().equals("AGENDADO")).collect(Collectors.toList());
    }
    public List<Atendimento> buscarPorSalaEDataHora(Sala sala, LocalDateTime dataHora){
        return bancoDeDados.stream().filter(a->a.getSala().equals(sala) && a.getDataHora().equals(dataHora) && a.getStatus().equals("AGENDADO")).collect(Collectors.toList());
    }
    public void limpar(){ bancoDeDados.clear(); contadorId.set(0); }
}
