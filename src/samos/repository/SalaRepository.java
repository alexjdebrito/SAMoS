package samos.repository;

import samos.model.Sala;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SalaRepository {
    private final List<Sala> bancoDeDados = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);
    public Sala salvar(Sala sala){ if(sala.getId()==null) sala.setId(contadorId.incrementAndGet()); else bancoDeDados.removeIf(s->s.getId().equals(sala.getId())); bancoDeDados.add(sala); return sala; }
    public Optional<Sala> buscarPorId(Long id){ return bancoDeDados.stream().filter(s->s.getId().equals(id)).findFirst(); }
    public List<Sala> buscarTodos(){ return new ArrayList<>(bancoDeDados); }
}
