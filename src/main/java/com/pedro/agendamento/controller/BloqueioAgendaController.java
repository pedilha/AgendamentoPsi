package com.pedro.agendamento.controller;

import com.pedro.agendamento.entity.BloqueioAgenda;
import com.pedro.agendamento.entity.Disponibilidade;
import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.BloqueioAgendaRepository;
import com.pedro.agendamento.repository.PsicologoRepository;
import com.pedro.agendamento.service.BloqueioAgendaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class BloqueioAgendaController {

    private final BloqueioAgendaRepository bloqueioAgendaRepository;
    private final PsicologoRepository psicologoRepository;
    private final BloqueioAgendaService bloqueioAgendaService;

    public BloqueioAgendaController(BloqueioAgendaRepository bloqueioAgendaRepository,
                                    PsicologoRepository psicologoRepository,
                                      BloqueioAgendaService bloqueioAgendaService) {
        this.bloqueioAgendaRepository = bloqueioAgendaRepository;
        this.psicologoRepository = psicologoRepository;
        this.bloqueioAgendaService = bloqueioAgendaService;
    }

    @GetMapping("/psicologo/bloqueio")
    public String listar(Authentication authentication, Model model) {
        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        List<BloqueioAgenda> bloqueios = bloqueioAgendaRepository.findByPsicologoId(psicologo.getId());

        model.addAttribute("bloqueios", bloqueios);
        return "bloqueio-lista";
    }

    @GetMapping("/psicologo/bloqueio/novo")
    public String formulario() {
        return "bloqueio-form";
    }

    @PostMapping("/psicologo/bloqueio/novo")
    public String cadastrar(Authentication authentication,
                             @RequestParam LocalDate data,
                             @RequestParam LocalTime horaInicio,
                             @RequestParam LocalTime horaFim,
                             @RequestParam String motivo) {

        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        bloqueioAgendaService.cadastrar(psicologo, data, horaInicio, horaFim, motivo);

        return "redirect:/psicologo/bloqueio";
    }

    @GetMapping ("/psicologo/bloqueio/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        BloqueioAgenda bloqueio = bloqueioAgendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bloqueio não encontrado."));

        model.addAttribute("bloqueio", bloqueio);
        return "bloqueio-editar";
    }

    @PostMapping ("/psicologo/bloqueio/{id}/editar")
    public String atualizar(@PathVariable Long id,
                             Authentication authentication,
                             @RequestParam LocalDate data,
                             @RequestParam LocalTime horaInicio,
                             @RequestParam LocalTime horaFim,
                             @RequestParam String motivo) {

        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        bloqueioAgendaService.atualizar(id, psicologo, data, horaInicio, horaFim, motivo);

        return "redirect:/psicologo/bloqueio";
    }

    @PostMapping ("/psicologo/bloqueio/{id}/excluir")
    public String excluir(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado para: " + email));

        bloqueioAgendaService.excluir(id, psicologo);

        return "redirect:/psicologo/bloqueio";
    }
}