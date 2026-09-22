package com.pedro.agendamento.controller;

import com.pedro.agendamento.entity.Disponibilidade;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.DisponibilidadeRepository;
import com.pedro.agendamento.repository.PsicologoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DisponibilidadeController {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PsicologoRepository psicologoRepository;

    public DisponibilidadeController(DisponibilidadeRepository disponibilidadeRepository,
                                      PsicologoRepository psicologoRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.psicologoRepository = psicologoRepository;
    }

    @GetMapping("/psicologo/disponibilidade")
    public String listar(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByPsicologoId(psicologo.getId());

        model.addAttribute("disponibilidades", disponibilidades);
        return "disponibilidade-lista";
    }
}