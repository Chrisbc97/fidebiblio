package com.fidebiblio.service;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean soloActivos) {
        return soloActivos ? usuarioRepository.findByActivoTrue() : usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ese correo"));
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscar(String termino) {
        return usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCase(termino, termino);
    }

    // Registrar usuario, valida duplicados
    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByIdentificacion(usuario.getIdentificacion())) {
            throw new IllegalArgumentException("Ya existe un usuario con esa identificación");
        }
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    // Actualizar usuario
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        existente.setNombre(usuario.getNombre());
        existente.setPrimerApellido(usuario.getPrimerApellido());
        existente.setSegundoApellido(usuario.getSegundoApellido());
        existente.setCorreo(usuario.getCorreo());
        existente.setTelefono(usuario.getTelefono());
        existente.setRol(usuario.getRol());
        return usuarioRepository.save(existente);
    }

    // Desactivar usuario
    @Transactional
    public void desactivar(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}