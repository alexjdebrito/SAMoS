package samos.repository;

import samos.model.RegistroClinico;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RegistroClinicoRepository {
    private final List<RegistroClinico> bancoDeDados = new ArrayList<>();
    private final AtomicLong contadorId = new AtomicLong(0);
    public RegistroClinico salvar(RegistroClinico registro){
        if(registro.getId()==null) registro.setId(contadorId.incrementAndGet());
        else bancoDeDados.removeIf(r->r.getId().equals(registro.getId()));
        bancoDeDados.add(registro); return registro;
    }
    public Optional<RegistroClinico> buscarPorId(Long id){ return bancoDeDados.stream().filter(r->r.getId().equals(id)).findFirst(); }
    public List<RegistroClinico> buscarPorIdAtendimento(Long idAtendimento){ return bancoDeDados.stream().filter(r->r.getAtendimento().getId().equals(idAtendimento)).collect(Collectors.toList()); }
    public List<RegistroClinico> buscarTodos(){ return new ArrayList<>(bancoDeDados); }
}
