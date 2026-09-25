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

    public void excluir(Long id, Psicologo psicologo) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada."));

        if (!disponibilidade.getPsicologo().getId().equals(psicologo.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para excluir esta disponibilidade.");
        }

        disponibilidadeRepository.delete(disponibilidade);
    }

    public Disponibilidade atualizar(Long id, Psicologo psicologo,
                                  DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {

        Disponibilidade existente = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada."));
    
        if (!existente.getPsicologo().getId().equals(psicologo.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para atualizar esta disponibilidade.");
        }
    
        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("Hora de início deve ser antes da hora de fim.");
        }
    
        List<Disponibilidade> outras = disponibilidadeRepository.findByPsicologoId(psicologo.getId());
    
        boolean colide = outras.stream()
                .filter(d -> !d.getId().equals(id) && d.getDiaSemana() == diaSemana)
                .anyMatch(d -> horaInicio.isBefore(d.getHoraFim()) && d.getHoraInicio().isBefore(horaFim));
    
        if (colide) {
            throw new IllegalArgumentException("Horário conflita com uma disponibilidade já cadastrada.");
        }
    
        Disponibilidade atualizada = new Disponibilidade(id, psicologo, diaSemana, horaInicio, horaFim);
        return disponibilidadeRepository.save(atualizada);
    }
}