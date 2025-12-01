package samos.service;

import samos.model.*;
import samos.repository.RegistroClinicoRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DocumentacaoService {
    private final RegistroClinicoRepository registroRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public DocumentacaoService(RegistroClinicoRepository registroRepository){ this.registroRepository = registroRepository; }
    public String emitirAtestadoComparecimento(Long idAtendimento){
        List<RegistroClinico> registros = registroRepository.buscarPorIdAtendimento(idAtendimento);
        if(registros.isEmpty()) throw new RuntimeException("Não há registro clínico para emitir o atestado.");
        RegistroClinico registro = registros.get(0);
        Atendimento atendimento = registro.getAtendimento();
        if(!"FINALIZADO".equals(atendimento.getStatus())) throw new IllegalStateException("O atendimento precisa estar FINALIZADO para a emissão de documentos oficiais.");
        return String.format("--- ATESTADO DE COMPARECIMENTO (SAMoS) ---\nAtesto, para os devidos fins, que o(a) paciente **%s**, CPF %s, \ncompareceu à Clínica Escola em **%s** para atendimento de **%s**.\nO atendimento foi realizado pelo profissional **%s** (%s).\n\nJoão Pessoa, %s.\n------------------------------------------", atendimento.getPaciente().getNome(), atendimento.getPaciente().getCpf(), atendimento.getDataHora().format(formatter), atendimento.getTipoServico(), atendimento.getProfissional().getNome(), atendimento.getProfissional().getTipoPessoa(), registro.getDataRegistro().toLocalDate().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")) );
    }
}
