package com.usuarios.gestionusuarios.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String correo;

    @NotBlank
    private String contrasena;

}
