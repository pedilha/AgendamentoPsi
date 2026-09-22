package com.pedro.agendamento.repository;

import com.pedro.agendamento.entity.Psicologo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    Optional<Psicologo> findByUsuarioEmail(String email);
}
