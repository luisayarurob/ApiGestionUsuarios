package com.usuarios.gestionusuarios.interfaces;

import com.usuarios.gestionusuarios.model.Estado;

import java.util.List;
import java.util.Optional;

public interface IEstadoService {

    List<Estado> findAll();

    Optional<Estado> findById(Long id);

    Optional<Estado> findByNombre(String nombre);

    Estado save(Estado estado);

    void deleteById(Long id);

    boolean existsByNombre(String nombre);

}
