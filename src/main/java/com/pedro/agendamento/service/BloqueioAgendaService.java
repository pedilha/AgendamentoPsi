package com.pedro.agendamento.service;

import com.pedro.agendamento.entity.BloqueioAgenda;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.BloqueioAgendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BloqueioAgendaService {

    private final BloqueioAgendaRepository bloqueioAgendaRepository;

    public BloqueioAgendaService(BloqueioAgendaRepository bloqueioAgendaRepository) {
        this.bloqueioAgendaRepository = bloqueioAgendaRepository;
    }

    public BloqueioAgenda cadastrar(Psicologo psicologo, LocalDate data,
                                     LocalTime horaInicio, LocalTime horaFim, String motivo) {

        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("Hora de início deve ser antes da hora de fim.");
        }

        List<BloqueioAgenda> existentes = bloqueioAgendaRepository.findByPsicologoId(psicologo.getId());

        boolean colide = existentes.stream()
                .filter(b -> b.getData().equals(data))
                .anyMatch(b -> horaInicio.isBefore(b.getHoraFim()) && b.getHoraInicio().isBefore(horaFim));

        if (colide) {
            throw new IllegalArgumentException("Horário conflita com um bloqueio já cadastrado.");
        }

        BloqueioAgenda novo = new BloqueioAgenda(psicologo, data, horaInicio, horaFim, motivo);
        return bloqueioAgendaRepository.save(novo);
    }
}