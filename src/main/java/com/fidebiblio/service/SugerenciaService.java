package com.fidebiblio.service;

import com.fidebiblio.domain.Sugerencia;
import com.fidebiblio.domain.Usuario;
import com.fidebiblio.repository.SugerenciaRepository;
import com.fidebiblio.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SugerenciaService {

    private final SugerenciaRepository sugerenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public SugerenciaService(SugerenciaRepository sugerenciaRepository, UsuarioRepository usuarioRepository) {
        this.sugerenciaRepository = sugerenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Sugerencia> getSugerencias() {
        return sugerenciaRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Registrar sugerencia
    @Transactional
    public Sugerencia proponer(Integer idUsuario, String titulo, String autor) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el usuario"));

        Optional<Sugerencia> existenteOpt = sugerenciaRepository.buscarPendientePorTitulo(titulo, "PENDIENTE");

        if (existenteOpt.isPresent()) {
            Sugerencia existente = existenteOpt.get();
            existente.setVotos(existente.getVotos() + 1);
            return sugerenciaRepository.save(existente);
        }

        Sugerencia sugerencia = new Sugerencia();
        sugerencia.setUsuario(usuario);
        sugerencia.setTitulo(titulo);
        sugerencia.setAutor(autor);
        sugerencia.setEstado("PENDIENTE");
        return sugerenciaRepository.save(sugerencia);
    }

    // Cambiar estado, aprobar o rechazar por parte del bibliotecario
    @Transactional
    public Sugerencia cambiarEstado(Integer idSugerencia, String nuevoEstado) {
        Sugerencia sugerencia = sugerenciaRepository.findById(idSugerencia)
                .orElseThrow(() -> new IllegalArgumentException("No existe la sugerencia"));
        sugerencia.setEstado(nuevoEstado);
        return sugerenciaRepository.save(sugerencia);
    }
}