package com.pedro.agendamento.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // JPA exige um construtor sem argumentos (usa reflection pra instanciar)
    protected Usuario() {}

    public Usuario(String email, String senhaHash, Role role) {
        this.email = email;
        this.senhaHash = senhaHash;
        this.role = role;
    }

    // getters — sem setters pra id (nunca deve mudar depois de criado)
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public Role getRole() { return role; }

    public enum Role {
        PSICOLOGO, PACIENTE
    }
    
    
    
}