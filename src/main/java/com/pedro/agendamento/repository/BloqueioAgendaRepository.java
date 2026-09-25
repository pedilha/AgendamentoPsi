package com.pedro.agendamento.repository;

import com.pedro.agendamento.entity.BloqueioAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BloqueioAgendaRepository extends JpaRepository<BloqueioAgenda, Long> {
    List<BloqueioAgenda> findByPsicologoId(Long psicologoId);
}