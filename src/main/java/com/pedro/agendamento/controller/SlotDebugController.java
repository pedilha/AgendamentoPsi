package com.pedro.agendamento.controller;

import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.repository.PsicologoRepository;
import com.pedro.agendamento.service.SlotService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
public class SlotDebugController {

    private final PsicologoRepository psicologoRepository;
    private final SlotService slotService;

    public SlotDebugController(PsicologoRepository psicologoRepository, SlotService slotService) {
        this.psicologoRepository = psicologoRepository;
        this.slotService = slotService;
    }

    @GetMapping("/debug/slots")
    public List<LocalTime> testarSlots(Authentication authentication, @RequestParam LocalDate data) {
        String email = authentication.getName();
        Psicologo psicologo = psicologoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new IllegalStateException("Psicólogo não encontrado."));

        return slotService.buscarSlotsDisponiveis(psicologo, data);
    }
}