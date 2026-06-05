package com.usuarios.gestionusuarios.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String correo;

    @NotBlank
    private String contrasena;

    private Long idRol;

}
