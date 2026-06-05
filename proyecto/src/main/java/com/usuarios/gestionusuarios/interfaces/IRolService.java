package com.usuarios.gestionusuarios.interfaces;

import com.usuarios.gestionusuarios.model.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {

    List<Rol> findAll();

    Optional<Rol> findById(Long id);

    Optional<Rol> findByNombre(String nombre);

    Rol save(Rol rol);

    void deleteById(Long id);

    boolean existsByNombre(String nombre);

}
