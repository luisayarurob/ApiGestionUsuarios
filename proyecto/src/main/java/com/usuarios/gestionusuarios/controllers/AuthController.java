package com.usuarios.gestionusuarios.controllers;

import com.usuarios.gestionusuarios.dtos.LoginRequest;
import com.usuarios.gestionusuarios.dtos.LoginResponse;
import com.usuarios.gestionusuarios.dtos.RegisterRequest;
import com.usuarios.gestionusuarios.model.Estado;
import com.usuarios.gestionusuarios.model.Rol;
import com.usuarios.gestionusuarios.model.Usuario;
import com.usuarios.gestionusuarios.repository.EstadoRepository;
import com.usuarios.gestionusuarios.repository.RolRepository;
import com.usuarios.gestionusuarios.security.JwtUtil;
import com.usuarios.gestionusuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final EstadoRepository estadoRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService,
                          RolRepository rolRepository, EstadoRepository estadoRepository,
                          JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /api/auth/login
     * Autentica el usuario con correo y contraseña.
     * Devuelve token JWT si autenticación es exitosa.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Autenticar con AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena())
            );

            // Obtener usuario autenticado para recuperar el rol
            Optional<Usuario> usuario = usuarioService.findByCorreo(request.getCorreo());
            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
            }

            String rol = usuario.get().getRol().getNombre();
            String token = jwtUtil.generateToken(request.getCorreo(), rol);

            // Actualizar último acceso
            Usuario usuarioActualizar = usuario.get();
            usuarioActualizar.setUltimoAcceso(LocalDateTime.now());
            usuarioService.save(usuarioActualizar);

            Usuario usuarioEncontrado = usuario.get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "dd/MM/yyyy HH:mm" );
            return ResponseEntity.ok(
                new LoginResponse(
                    token,
                    usuarioEncontrado.getRol().getNombre(),
                    usuarioEncontrado.getNombre(),
                    usuarioEncontrado.getCorreo(),
                    usuarioEncontrado.getFechaRegistro() != null ? usuarioEncontrado .getFechaRegistro() .format(formatter) : "No disponible",
                    usuarioEncontrado.getEstado() != null? usuarioEncontrado.getEstado().getNombre(): "ACTIVO",
                    usuarioEncontrado.getUltimoAcceso() != null ? usuarioEncontrado .getUltimoAcceso() .format(formatter) : "Primer acceso"
                )
            );

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * POST /api/auth/register
     * Registra un nuevo usuario en el sistema.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Validar que el correo no exista
            if (usuarioService.existsByCorreo(request.getCorreo())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya está registrado");
            }

            // Obtener rol (por defecto USER si no se especifica)
            Long idRol = request.getIdRol() != null ? request.getIdRol() : 2L;
            Optional<Rol> rol = rolRepository.findById(idRol);
            if (rol.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol no válido");
            }

            // Obtener estado ACTIVO por defecto
            Optional<Estado> estado = estadoRepository.findByNombre("ACTIVO");
            if (estado.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado ACTIVO no existe en la base de datos");
            }

            // Crear nuevo usuario con contraseña encriptada
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(request.getNombre());
            nuevoUsuario.setCorreo(request.getCorreo());
            nuevoUsuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
            nuevoUsuario.setRol(rol.get());
            nuevoUsuario.setEstado(estado.get());
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());

            Usuario usuarioGuardado = usuarioService.save(nuevoUsuario);

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente con ID: " + usuarioGuardado.getId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor: " + e.getMessage());
        }
    }

}
