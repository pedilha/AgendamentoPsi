package com.pedro.agendamento.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "psicologo") 
public class Psicologo {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    @Column(nullable = false)
    private String crp;

    @Column(nullable = false)
    private Integer duracaoConsultaMinutos;

   protected Psicologo() {}

public Psicologo(Usuario usuario, String crp, Integer duracaoConsultaMinutos) {
    this.usuario = usuario;
    this.crp = crp;
    this.duracaoConsultaMinutos = duracaoConsultaMinutos;
}

public Long getId() { return id; }
public Usuario getUsuario() { return usuario; }
public String getCrp() { return crp; }
public Integer getDuracaoConsultaMinutos() { return duracaoConsultaMinutos; }

}
