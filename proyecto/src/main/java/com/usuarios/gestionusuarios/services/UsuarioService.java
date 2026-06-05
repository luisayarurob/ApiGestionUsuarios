package com.usuarios.gestionusuarios.services;

import com.usuarios.gestionusuarios.interfaces.IUsuarioService;
import com.usuarios.gestionusuarios.model.Usuario;
import com.usuarios.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

	private final UsuarioRepository usuarioRepository;

	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}

	@Override
	public Optional<Usuario> findById(Long id) {
		return usuarioRepository.findById(id);
	}

	@Override
	public Optional<Usuario> findByCorreo(String correo) {
		return usuarioRepository.findByCorreo(correo);
	}

	@Override
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	@Override
	public void deleteById(Long id) {
		usuarioRepository.deleteById(id);
	}

	@Override
	public boolean existsByCorreo(String correo) {
		return usuarioRepository.existsByCorreo(correo);
	}

}
