package com.usuarios.gestionusuarios.dtos;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private String tipo = "Bearer";

    private String rol;

    public LoginResponse() {}

    public LoginResponse(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }

}
