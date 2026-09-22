package com.pedro.agendamento;

import com.pedro.agendamento.entity.Psicologo;
import com.pedro.agendamento.entity.Usuario;
import com.pedro.agendamento.repository.PsicologoRepository;
import com.pedro.agendamento.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PsicologoRepository psicologoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                       PsicologoRepository psicologoRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.psicologoRepository = psicologoRepository;
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
            usuario = usuarioRepository.save(usuario);

            Psicologo psicologo = new Psicologo(usuario, "CRP-01/12345", 50);
            psicologoRepository.save(psicologo);
        }
    }
}