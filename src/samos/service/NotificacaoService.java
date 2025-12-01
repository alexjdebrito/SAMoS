package samos.service;

import samos.model.Atendimento;

import java.time.format.DateTimeFormatter;

public class NotificacaoService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    public void enviarLembrete(Atendimento atendimento){
        String mensagem = String.format("🔔 LEMBRETE SAMoS: Olá, %s! Você tem um agendamento de %s \ncom %s, no dia %s, na Sala %s. Por favor, seja pontual.", atendimento.getPaciente().getNome(), atendimento.getTipoServico(), atendimento.getProfissional().getNome(), atendimento.getDataHora().format(formatter), atendimento.getSala().getNome());
        System.out.println("\n--- [NOTIFICAÇÃO ENVIADA] ---"); System.out.println(mensagem); System.out.println("-----------------------------");
    }
}
