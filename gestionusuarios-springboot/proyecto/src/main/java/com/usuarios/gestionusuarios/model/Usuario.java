package com.usuarios.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // FK hacia Roles
    @ManyToOne
    @JoinColumn(name = "IdRol", nullable = false)
    private Rol rol;

    // FK hacia Estados
    @ManyToOne
    @JoinColumn(name = "IdEstado", nullable = false)
    private Estado estado;

}
