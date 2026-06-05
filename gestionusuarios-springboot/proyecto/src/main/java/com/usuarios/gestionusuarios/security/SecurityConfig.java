package com.usuarios.gestionusuarios.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Reglas de seguridad HTTP:
     *
     * Rutas públicas:
     *   POST /api/auth/login    → cualquiera
     *   POST /api/auth/register → cualquiera
     *
     * Rutas exclusivas de ADMIN:
     *   GET    /api/usuarios          → listar todos
     *   DELETE /api/usuarios/{id}     → eliminar cualquier usuario (no puede eliminarse a sí mismo, lógica en controller)
     *
     * Rutas de cualquier usuario autenticado:
     *   GET    /api/usuarios/perfil         → ver su perfil
     *   PUT    /api/usuarios/perfil         → editar su propio perfil  (manejado en PUT /{id} con validación)
     *   DELETE /api/usuarios/perfil/baja    → dar de baja su propia cuenta
     *   GET    /api/usuarios/{id}           → ver usuario (controller valida que sea él o admin)
     *   PUT    /api/usuarios/{id}           → editar usuario (controller valida que sea él o admin)
     *
     * Nota: CustomUserDetailsService registra los roles como "ROLE_" + nombre.toUpperCase()
     * Por eso usamos hasRole("ADMIN") que Spring transforma internamente a "ROLE_ADMIN".
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Solo ADMIN puede listar todos los usuarios y eliminar por ID
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasRole("ADMIN")

                // Cualquier usuario autenticado puede acceder al resto de /api/usuarios/**
                .requestMatchers("/api/usuarios/**").authenticated()

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
