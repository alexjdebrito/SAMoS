package samos.service;

import samos.model.*;

public class GestaoFuncionariosService {
    public void associarEstagiarioSupervisor(Estagiario estagiario, Supervisor supervisor){
        if(estagiario.getSupervisor()!=null) estagiario.getSupervisor().removerEstagiario(estagiario);
        estagiario.setSupervisor(supervisor);
        supervisor.adicionarEstagiario(estagiario);
        System.out.println("✅ Associação realizada: " + estagiario.getNome() + " agora é supervisionado por " + supervisor.getNome());
    }
}
