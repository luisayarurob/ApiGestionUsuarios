package com.usuarios.gestionusuarios.security;

import com.usuarios.gestionusuarios.model.Usuario;
import com.usuarios.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getEstado() == null || usuario.getEstado().getNombre() == null
                || !usuario.getEstado().getNombre().equalsIgnoreCase("ACTIVO")) {
            throw new UsernameNotFoundException("Usuario no activo");
        }

        String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USER";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase());

        return new User(usuario.getCorreo(), usuario.getContrasena(), Collections.singletonList(authority));
    }

}
