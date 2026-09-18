package com.pedro.agendamento.entity;

import jakarta.persistence.*;

@Entity 
@Table(name = "paciente")
public class Paciente {
    
    @Id 
    private Long id;

    @OneToOne 
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    @Column(nullable = false)
    private String telefone;

    protected Paciente() {}

    public Paciente(Usuario usuario, String telefone) {
        this.usuario = usuario;
        this.telefone = telefone;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getTelefone() { return telefone; }
}