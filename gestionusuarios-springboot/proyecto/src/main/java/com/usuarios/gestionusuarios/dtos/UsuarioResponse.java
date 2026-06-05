package com.usuarios.gestionusuarios.dtos;

import com.usuarios.gestionusuarios.model.Usuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    public static UsuarioResponse from(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol().getNombre());
        dto.setEstado(usuario.getEstado().getNombre());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        return dto;
    }
}
