package com.pedro.agendamento.service;

import com.pedro.agendamento.entity.*;
import com.pedro.agendamento.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SlotServiceTest {

    private DisponibilidadeRepository disponibilidadeRepository;
    private BloqueioAgendaRepository bloqueioAgendaRepository;
    private AgendamentoRepository agendamentoRepository;
    private SlotService slotService;
    private Psicologo psicologo;

    @BeforeEach
    void setUp() {
        disponibilidadeRepository = mock(DisponibilidadeRepository.class);
        bloqueioAgendaRepository = mock(BloqueioAgendaRepository.class);
        agendamentoRepository = mock(AgendamentoRepository.class);

        slotService = new SlotService(disponibilidadeRepository, bloqueioAgendaRepository, agendamentoRepository);

        Usuario usuario = new Usuario("psicologa@teste.com", "hash", Usuario.Role.PSICOLOGO);
        psicologo = new Psicologo(usuario, "CRP-01/00000", 50);
    }

    @Test
    void deveGerarSlotsQuandoNaoHaBloqueioNemAgendamento() {
        LocalDate segunda = LocalDate.of(2026, 9, 28);
        Disponibilidade disponibilidade = new Disponibilidade(psicologo, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        when(disponibilidadeRepository.findByPsicologoId(any())).thenReturn(List.of(disponibilidade));
        when(bloqueioAgendaRepository.findByPsicologoId(any())).thenReturn(List.of());
        when(agendamentoRepository.findByPsicologoId(any())).thenReturn(List.of());

        List<LocalTime> slots = slotService.buscarSlotsDisponiveis(psicologo, segunda);

        assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 50)), slots);
    }

    @Test
    void deveRemoverSlotQueColideComBloqueio() {
        LocalDate segunda = LocalDate.of(2026, 9, 28);
        Disponibilidade disponibilidade = new Disponibilidade(psicologo, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        BloqueioAgenda bloqueio = new BloqueioAgenda(psicologo, segunda, LocalTime.of(8, 0), LocalTime.of(8, 30), "teste");

        when(disponibilidadeRepository.findByPsicologoId(any())).thenReturn(List.of(disponibilidade));
        when(bloqueioAgendaRepository.findByPsicologoId(any())).thenReturn(List.of(bloqueio));
        when(agendamentoRepository.findByPsicologoId(any())).thenReturn(List.of());

        List<LocalTime> slots = slotService.buscarSlotsDisponiveis(psicologo, segunda);

        assertEquals(List.of(LocalTime.of(8, 50)), slots);
    }

    @Test
    void deveRemoverSlotQueColideComAgendamento() {
        LocalDate segunda = LocalDate.of(2026, 9, 28);
        Disponibilidade disponibilidade = new Disponibilidade(psicologo, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        Paciente paciente = new Paciente(new Usuario("paciente@teste.com", "hash", Usuario.Role.PACIENTE), "62999999999");
        Agendamento agendamento = new Agendamento(psicologo, paciente, LocalDateTime.of(segunda, LocalTime.of(8, 50)));

        when(disponibilidadeRepository.findByPsicologoId(any())).thenReturn(List.of(disponibilidade));
        when(bloqueioAgendaRepository.findByPsicologoId(any())).thenReturn(List.of());
        when(agendamentoRepository.findByPsicologoId(any())).thenReturn(List.of(agendamento));

        List<LocalTime> slots = slotService.buscarSlotsDisponiveis(psicologo, segunda);

        assertEquals(List.of(LocalTime.of(8, 0)), slots);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaDisponibilidadeNoDia() {
        LocalDate terca = LocalDate.of(2026, 9, 29);

        when(disponibilidadeRepository.findByPsicologoId(any())).thenReturn(List.of());
        when(bloqueioAgendaRepository.findByPsicologoId(any())).thenReturn(List.of());
        when(agendamentoRepository.findByPsicologoId(any())).thenReturn(List.of());

        List<LocalTime> slots = slotService.buscarSlotsDisponiveis(psicologo, terca);

        assertTrue(slots.isEmpty());
    }
}