package com.pedro.agendamento.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id", nullable = false)
    private Psicologo psicologo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    protected Agendamento() {}

    public Agendamento(Psicologo psicologo, Paciente paciente, LocalDateTime dataHora) {
        this.psicologo = psicologo;
        this.paciente = paciente;
        this.dataHora = dataHora;
        this.status = Status.CONFIRMADO;
    }

    public Long getId() { return id; }
    public Psicologo getPsicologo() { return psicologo; }
    public Paciente getPaciente() { return paciente; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Status getStatus() { return status; }

    public enum Status {
        PENDENTE, CONFIRMADO, CANCELADO, REALIZADO
    }
}