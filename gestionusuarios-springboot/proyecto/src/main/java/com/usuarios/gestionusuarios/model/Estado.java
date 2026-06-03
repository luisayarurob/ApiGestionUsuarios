package com.usuarios.gestionusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEstado")
    private Long idEstado;

    @Column(nullable = false, unique = true)
    private String nombre;

}
