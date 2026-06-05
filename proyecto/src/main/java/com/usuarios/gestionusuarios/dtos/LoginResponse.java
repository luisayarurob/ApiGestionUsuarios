package com.usuarios.gestionusuarios.dtos;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private String tipo = "Bearer";

    private String rol;

    private String nombre;

    private String correo;

    private String fechaRegistro;

    private String estado;

    private String ultimoAcceso;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String rol,
            String nombre,
            String correo,
            String fechaRegistro,
            String estado,
            String ultimoAcceso
    ) {

        this.token = token;
        this.rol = rol;
        this.nombre = nombre;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.ultimoAcceso = ultimoAcceso;
    }
}