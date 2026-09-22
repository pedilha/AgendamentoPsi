package com.pedro.agendamento.repository;

import com.pedro.agendamento.entity.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {
    List<Disponibilidade> findByPsicologoId(Long psicologoId);
}