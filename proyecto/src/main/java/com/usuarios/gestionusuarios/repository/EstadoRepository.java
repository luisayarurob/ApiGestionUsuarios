package com.usuarios.gestionusuarios.repository;

import com.usuarios.gestionusuarios.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {

    Optional<Estado> findByNombre(String nombre);

}
