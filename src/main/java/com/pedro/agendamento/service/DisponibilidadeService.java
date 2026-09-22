package com.pedro.agendamento.service;

import com.pedro.agendamento.entity.Disponibilidade;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.DisponibilidadeRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    public Disponibilidade cadastrar(Psicologo psicologo, DayOfWeek diaSemana,
                                      LocalTime horaInicio, LocalTime horaFim) {

        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("Hora de início deve ser antes da hora de fim.");
        }

        List<Disponibilidade> existentes = disponibilidadeRepository.findByPsicologoId(psicologo.getId());

        boolean colide = existentes.stream()
                .filter(d -> d.getDiaSemana() == diaSemana)
                .anyMatch(d -> horaInicio.isBefore(d.getHoraFim()) && d.getHoraInicio().isBefore(horaFim));

        if (colide) {
            throw new IllegalArgumentException("Horário conflita com uma disponibilidade já cadastrada.");
        }

        Disponibilidade nova = new Disponibilidade(psicologo, diaSemana, horaInicio, horaFim);
        return disponibilidadeRepository.save(nova);
    }
}