package samos.service;

import samos.model.*;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TriagemService {
    private final PriorityQueue<Triagem> filaPrioritaria;
    public TriagemService(){ this.filaPrioritaria = new PriorityQueue<>(Comparator.comparing(Triagem::getPrioridade).reversed()); }
    public Triagem realizarTriagem(Paciente paciente, String motivo, Triagem.NivelPrioridade prioridade){ Triagem triagem = new Triagem(paciente, prioridade, motivo); filaPrioritaria.add(triagem); System.out.println("✅ Triagem concluída. Paciente " + paciente.getNome() + " adicionado à fila com prioridade " + prioridade.name()); return triagem; }
    public Triagem chamarProximoPaciente(){ if(filaPrioritaria.isEmpty()) return null; return filaPrioritaria.poll(); }
    public int getTamanhoFila(){ return filaPrioritaria.size(); }
}
