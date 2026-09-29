package com.pedro.agendamento.service;

import com.pedro.agendamento.entity.Disponibilidade;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.entity.BloqueioAgenda;
import com.pedro.agendamento.entity.Agendamento;
import com.pedro.agendamento.repository.DisponibilidadeRepository;
import com.pedro.agendamento.repository.BloqueioAgendaRepository;
import com.pedro.agendamento.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {
    
    private final BloqueioAgendaRepository bloqueioAgendaRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final AgendamentoRepository agendamentoRepository;

    public SlotService(DisponibilidadeRepository disponibilidadeRepository,
                        BloqueioAgendaRepository bloqueioAgendaRepository,
                        AgendamentoRepository agendamentoRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.bloqueioAgendaRepository = bloqueioAgendaRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public List<Disponibilidade> buscarDisponibilidadeDoDia(Psicologo psicologo, LocalDate data) {
        DayOfWeek diaSemana = data.getDayOfWeek();

        List<Disponibilidade> todasDoPsicologo = disponibilidadeRepository.findByPsicologoId(psicologo.getId());

        return todasDoPsicologo.stream()
                .filter(d -> d.getDiaSemana() == diaSemana)
                .toList();
    }

    private List<LocalTime> gerarSlotsCandidatos(LocalTime horaInicio, LocalTime horaFim, int duracaoMinutos) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime atual = horaInicio;

        while (!atual.plusMinutes(duracaoMinutos).isAfter(horaFim)) {
            slots.add(atual);
            atual = atual.plusMinutes(duracaoMinutos);
        }

        return slots;
    }

    public List<LocalTime> buscarSlotsDisponiveis(Psicologo psicologo, LocalDate data) {
        List<Disponibilidade> disponibilidadesDoDia = buscarDisponibilidadeDoDia(psicologo, data);
        List<BloqueioAgenda> bloqueiosDoDia = buscarBloqueiosDoDia(psicologo, data);
        List<Agendamento> agendamentosDoDia = buscarAgendamentosDoDia(psicologo, data);

        List<LocalTime> todosOsSlots = new ArrayList<>();

        for (Disponibilidade disponibilidade : disponibilidadesDoDia) {
            List<LocalTime> slots = gerarSlotsCandidatos(
                    disponibilidade.getHoraInicio(),
                    disponibilidade.getHoraFim(),
                    psicologo.getDuracaoConsultaMinutos()
            );
            todosOsSlots.addAll(slots);
        }

        int duracaoMinutos = psicologo.getDuracaoConsultaMinutos();

        return todosOsSlots.stream()
                .filter(slot -> !colideComBloqueio(slot, slot.plusMinutes(duracaoMinutos), bloqueiosDoDia))
                .filter(slot -> !colideComAgendamento(slot, slot.plusMinutes(duracaoMinutos), agendamentosDoDia, duracaoMinutos))
                .toList();
    }

    private List<BloqueioAgenda> buscarBloqueiosDoDia(Psicologo psicologo, LocalDate data) {
        List<BloqueioAgenda> todosDoPsicologo = bloqueioAgendaRepository.findByPsicologoId(psicologo.getId());

        return todosDoPsicologo.stream()
                .filter(b -> b.getData().equals(data))
                .toList();
    }

    private boolean colideComBloqueio(LocalTime slotInicio, LocalTime slotFim, List<BloqueioAgenda> bloqueiosDoDia) {
        return bloqueiosDoDia.stream()
                .anyMatch(b -> slotInicio.isBefore(b.getHoraFim()) && b.getHoraInicio().isBefore(slotFim));
    }

    private List<Agendamento> buscarAgendamentosDoDia(Psicologo psicologo, LocalDate data) {
        List<Agendamento> todosDoPsicologo = agendamentoRepository.findByPsicologoId(psicologo.getId());

        return todosDoPsicologo.stream()
                .filter(a -> a.getDataHora().toLocalDate().equals(data))
                .toList();
    }

    private boolean colideComAgendamento(LocalTime slotInicio, LocalTime slotFim, List<Agendamento> agendamentosDoDia, int duracaoMinutos) {
        return agendamentosDoDia.stream()
                .anyMatch(a -> {
                    LocalTime agendamentoInicio = a.getDataHora().toLocalTime();
                    LocalTime agendamentoFim = agendamentoInicio.plusMinutes(duracaoMinutos);
                    return slotInicio.isBefore(agendamentoFim) && agendamentoInicio.isBefore(slotFim);
                });
    }
    

}