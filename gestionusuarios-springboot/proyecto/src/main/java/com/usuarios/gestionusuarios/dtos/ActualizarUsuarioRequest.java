package com.usuarios.gestionusuarios.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarUsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    // Opcional: si se envía, se actualiza la contraseña
    private String contrasena;

    // Opcional: si el admin quiere cambiar rol o estado
    private Long idRol;
    private Long idEstado;
}
