package com.pedro.agendamento;

import com.pedro.agendamento.entity.Usuario;
import com.pedro.agendamento.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("psicologa@teste.com").isEmpty()) {
            Usuario usuario = new Usuario(
                "psicologa@teste.com",
                passwordEncoder.encode("123456"),
                Usuario.Role.PSICOLOGO
            );
            usuarioRepository.save(usuario);
        }
    }
}