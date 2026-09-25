package com.pedro.agendamento.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "bloqueio_agenda")
public class BloqueioAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Column(nullable = false)
    private String motivo;

    protected BloqueioAgenda() {}

    public BloqueioAgenda(Psicologo psicologo, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String motivo) {
        this.psicologo = psicologo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.motivo = motivo;
    }

    public BloqueioAgenda(Long id, Psicologo psicologo, LocalDate data, LocalTime horaInicio, LocalTime horaFim, String motivo) {
        this.id = id;
        this.psicologo = psicologo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public Psicologo getPsicologo() { return psicologo; }
    public LocalDate getData() { return data; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public String getMotivo() { return motivo; }
}