package com.usuarios.gestionusuarios.config;

import com.usuarios.gestionusuarios.model.Estado;
import com.usuarios.gestionusuarios.model.Rol;
import com.usuarios.gestionusuarios.model.Usuario;
import com.usuarios.gestionusuarios.repository.EstadoRepository;
import com.usuarios.gestionusuarios.repository.RolRepository;
import com.usuarios.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeederConfig implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final EstadoRepository estadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeederConfig(RolRepository rolRepository, EstadoRepository estadoRepository,
                            UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            Rol adminRol = new Rol();
            adminRol.setNombre("ADMIN");
            rolRepository.save(adminRol);
            System.out.println("✓ Rol ADMIN creado");
        }

        if (rolRepository.findByNombre("USER").isEmpty()) {
            Rol userRol = new Rol();
            userRol.setNombre("USER");
            rolRepository.save(userRol);
            System.out.println("✓ Rol USER creado");
        }

        // Crear estados si no existen
        if (estadoRepository.findByNombre("ACTIVO").isEmpty()) {
            Estado activoEstado = new Estado();
            activoEstado.setNombre("ACTIVO");
            estadoRepository.save(activoEstado);
            System.out.println("✓ Estado ACTIVO creado");
        }

        if (estadoRepository.findByNombre("INACTIVO").isEmpty()) {
            Estado inactivoEstado = new Estado();
            inactivoEstado.setNombre("INACTIVO");
            estadoRepository.save(inactivoEstado);
            System.out.println("✓ Estado INACTIVO creado");
        }

        // Crear usuario administrador si no existe
        if (usuarioRepository.findByCorreo("admin@test.com").isEmpty()) {
            Usuario adminUsuario = new Usuario();
            adminUsuario.setNombre("Administrador");
            adminUsuario.setCorreo("admin@test.com");
            adminUsuario.setContrasena(passwordEncoder.encode("admin123"));
            adminUsuario.setRol(rolRepository.findByNombre("ADMIN").orElseThrow());
            adminUsuario.setEstado(estadoRepository.findByNombre("ACTIVO").orElseThrow());
            adminUsuario.setFechaRegistro(LocalDateTime.now());

            usuarioRepository.save(adminUsuario);
            System.out.println("✓ Usuario ADMIN creado (correo: admin@test.com, password: admin123)");
        }

        // Crear usuario de prueba si no existe
        if (usuarioRepository.findByCorreo("user@test.com").isEmpty()) {
            Usuario testUsuario = new Usuario();
            testUsuario.setNombre("Usuario Test");
            testUsuario.setCorreo("user@test.com");
            testUsuario.setContrasena(passwordEncoder.encode("user123"));
            testUsuario.setRol(rolRepository.findByNombre("USER").orElseThrow());
            testUsuario.setEstado(estadoRepository.findByNombre("ACTIVO").orElseThrow());
            testUsuario.setFechaRegistro(LocalDateTime.now());

            usuarioRepository.save(testUsuario);
            System.out.println("✓ Usuario TEST creado (correo: user@test.com, password: user123)");
        }

    }

}
