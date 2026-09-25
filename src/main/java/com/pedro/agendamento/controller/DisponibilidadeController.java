package com.pedro.agendamento.controller;

import com.pedro.agendamento.entity.Disponibilidade;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.DisponibilidadeRepository;
import com.pedro.agendamento.repository.PsicologoRepository;
import com.pedro.agendamento.service.DisponibilidadeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Controller
public class DisponibilidadeController {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final PsicologoRepository psicologoRepository;
    private final DisponibilidadeService disponibilidadeService;

    public DisponibilidadeController(DisponibilidadeRepository disponibilidadeRepository,
                                      PsicologoRepository psicologoRepository,
                                      DisponibilidadeService disponibilidadeService) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.psicologoRepository = psicologoRepository;
        this.disponibilidadeService = disponibilidadeService;
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

    @GetMapping("/psicologo/disponibilidade/nova")
    public String formulario() {
        return "disponibilidade-form";
    }

    @PostMapping("/psicologo/disponibilidade/nova")
    public String cadastrar(Authentication authentication,
                             @RequestParam DayOfWeek diaSemana,
                             @RequestParam LocalTime horaInicio,
                             @RequestParam LocalTime horaFim) {

        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        disponibilidadeService.cadastrar(psicologo, diaSemana, horaInicio, horaFim);

        return "redirect:/psicologo/disponibilidade";
    }

    @GetMapping ("/psicologo/disponibilidade/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disponibilidade não encontrada."));

        model.addAttribute("disponibilidade", disponibilidade);
        return "disponibilidade-editar";
    }

    @PostMapping ("/psicologo/disponibilidade/{id}/editar")
    public String atualizar(@PathVariable Long id,
                             Authentication authentication,
                             @RequestParam DayOfWeek diaSemana,
                             @RequestParam LocalTime horaInicio,
                             @RequestParam LocalTime horaFim) {

        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        disponibilidadeService.atualizar(id, psicologo, diaSemana, horaInicio, horaFim);

        return "redirect:/psicologo/disponibilidade";
    }

    @PostMapping ("/psicologo/disponibilidade/{id}/excluir")
    public String excluir(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        disponibilidadeService.excluir(id, psicologo);

        return "redirect:/psicologo/disponibilidade";
    }
}