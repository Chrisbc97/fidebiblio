package com.fidebiblio.service;

import com.fidebiblio.repository.LibroRepository;
import com.fidebiblio.repository.MultaRepository;
import com.fidebiblio.repository.PrestamoRepository;
import com.fidebiblio.repository.UsuarioRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;
    private final MultaRepository multaRepository;

    public ReporteService(LibroRepository libroRepository, UsuarioRepository usuarioRepository,
            PrestamoRepository prestamoRepository, MultaRepository multaRepository) {
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
        this.multaRepository = multaRepository;
    }

    @Transactional(readOnly = true)
    public long getTotalLibros() {
        return libroRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTotalUsuarios() {
        return usuarioRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTotalPrestamos() {
        return prestamoRepository.count();
    }

    @Transactional(readOnly = true)
    public long getPrestamosActivos() {
        return prestamoRepository.buscarPorEstado("ACTIVO").size();
    }

    @Transactional(readOnly = true)
    public long getMultasPendientes() {
        return multaRepository.findByEstadoOrderByFechaGeneracionAsc("PENDIENTE").size();
    }

    // 5 libros más prestados
    @Transactional(readOnly = true)
    public Map<String, Long> getTop5LibrosMasPrestados() {
        List<Object[]> resultado = prestamoRepository.librosMasPrestados();
        Map<String, Long> top5 = new LinkedHashMap<>();
        int limite = Math.min(5, resultado.size());
        for (int i = 0; i < limite; i++) {
            Object[] fila = resultado.get(i);
            top5.put((String) fila[0], (Long) fila[1]);
        }
        return top5;
    }
}
