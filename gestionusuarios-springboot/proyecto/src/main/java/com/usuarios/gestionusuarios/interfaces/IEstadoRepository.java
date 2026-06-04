package com.usuarios.gestionusuarios.interfaces;

import com.usuarios.gestionusuarios.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IEstadoRepository extends JpaRepository<Estado, Long> {

    Optional<Estado> findByNombre(String nombre);

}
