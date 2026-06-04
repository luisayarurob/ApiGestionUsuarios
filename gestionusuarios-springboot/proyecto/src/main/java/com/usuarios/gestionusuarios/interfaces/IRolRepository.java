package com.usuarios.gestionusuarios.interfaces;

import com.usuarios.gestionusuarios.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

}
