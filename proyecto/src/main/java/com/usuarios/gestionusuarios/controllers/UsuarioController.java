package com.usuarios.gestionusuarios.controllers;

import com.usuarios.gestionusuarios.dtos.ActualizarUsuarioRequest;
import com.usuarios.gestionusuarios.dtos.UsuarioResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final EstadoRepository estadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UsuarioController(UsuarioService usuarioService,
                             RolRepository rolRepository,
                             EstadoRepository estadoRepository,
                             PasswordEncoder passwordEncoder,
                             JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // =========================================================
    // GET /api/usuarios  →  Solo ADMIN
    // Lista todos los usuarios del sistema.
    // =========================================================
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.findAll()
                .stream()
                .map(UsuarioResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    // =========================================================
    // GET /api/usuarios/{id}  →  ADMIN o el propio usuario
    // Obtiene un usuario por ID.
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id, Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que sea ADMIN o el propio usuario
        if (!esAdminOPropioUsuario(authentication, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver este recurso");
        }

        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    // =========================================================
    // GET /api/usuarios/perfil  →  Usuario autenticado
    // Devuelve los datos del usuario que hace la petición.
    // =========================================================
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        return ResponseEntity.ok(UsuarioResponse.from(usuarioOpt.get()));
    }

    // =========================================================
    // PUT /api/usuarios/{id}  →  ADMIN o el propio usuario
    // Actualiza nombre, correo, contraseña (opcional).
    // Solo ADMIN puede cambiar rol o estado.
    // =========================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @Valid @RequestBody ActualizarUsuarioRequest request,
                                               Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Solo ADMIN o el propio usuario pueden editar
        if (!esAdminOPropioUsuario(authentication, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar este usuario");
        }

        // Verificar que el nuevo correo no esté en uso por otro usuario
        if (!usuario.getCorreo().equals(request.getCorreo())
                && usuarioService.existsByCorreo(request.getCorreo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya está en uso por otro usuario");
        }

        // Actualizar campos básicos
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());

        // Actualizar contraseña solo si se envió
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        }

        // Solo ADMIN puede cambiar rol y estado
        if (esAdmin(authentication)) {
            if (request.getIdRol() != null) {
                Optional<Rol> rolOpt = rolRepository.findById(request.getIdRol());
                if (rolOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol no válido");
                }
                usuario.setRol(rolOpt.get());
            }

            if (request.getIdEstado() != null) {
                Optional<Estado> estadoOpt = estadoRepository.findById(request.getIdEstado());
                if (estadoOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado no válido");
                }
                usuario.setEstado(estadoOpt.get());
            }
        }

        Usuario actualizado = usuarioService.save(usuario);
        return ResponseEntity.ok(UsuarioResponse.from(actualizado));
    }

    // =========================================================
    // DELETE /api/usuarios/{id}  →  ADMIN o el propio usuario
    // Elimina (da de baja) al usuario.
    // ADMIN no puede eliminarse a sí mismo.
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, Authentication authentication) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // ADMIN no puede eliminarse a sí mismo
        if (authentication.getName().equals(usuario.getCorreo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No puedes eliminar tu propia cuenta desde este endpoint");
        }

        // Solo ADMIN o el propio usuario pueden eliminar
        if (!esAdminOPropioUsuario(authentication, usuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este usuario");
        }

        usuarioService.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    // =========================================================
    // DELETE /api/usuarios/perfil/baja  →  Usuario autenticado
    // El usuario da de baja su propia cuenta.
    // =========================================================
    @DeleteMapping("/perfil/baja")
    public ResponseEntity<?> darBajaPropiacuenta(Authentication authentication) {
        String correo = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        usuarioService.deleteById(usuarioOpt.get().getId());
        return ResponseEntity.ok("Tu cuenta ha sido eliminada correctamente");
    }

    // =========================================================
    // Métodos auxiliares privados
    // =========================================================

    private boolean esAdmin(Authentication authentication) {
        // CustomUserDetailsService registra los roles como "ROLE_" + nombre.toUpperCase()
        // El rol administrador en la BD se llama "admin", por tanto queda "ROLE_ADMIN"
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }

    private boolean esAdminOPropioUsuario(Authentication authentication, Usuario usuario) {
        String correoAutenticado = authentication.getName();
        return esAdmin(authentication) || correoAutenticado.equals(usuario.getCorreo());
    }
}