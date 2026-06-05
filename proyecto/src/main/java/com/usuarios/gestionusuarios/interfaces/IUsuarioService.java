package com.usuarios.gestionusuarios.interfaces;

import com.usuarios.gestionusuarios.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByCorreo(String correo);

    Usuario save(Usuario usuario);

    void deleteById(Long id);

    boolean existsByCorreo(String correo);

}
