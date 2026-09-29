package com.pedro.agendamento.repository;

import com.pedro.agendamento.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
        List<Agendamento> findByPsicologoId(Long psicologoId);
    }