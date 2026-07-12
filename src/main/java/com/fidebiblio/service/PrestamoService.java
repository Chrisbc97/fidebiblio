package com.fidebiblio.service;

import com.fidebiblio.domain.Libro;
import com.fidebiblio.domain.Prestamo;
import com.fidebiblio.domain.Usuario;
import com.fidebiblio.repository.LibroRepository;
import com.fidebiblio.repository.PrestamoRepository;
import com.fidebiblio.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoService {

    // Días de préstamo por defecto
    private static final int DIAS_PRESTAMO = 7;

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository,
            LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<Prestamo> getPrestamosActivos() {
        return prestamoRepository.buscarPorEstado("ACTIVO");
    }

    // Historial de préstamos del estudiante
    @Transactional(readOnly = true)
    public List<Prestamo> getHistorial(Integer idUsuario) {
        return prestamoRepository.buscarHistorialPorUsuario(idUsuario);
    }

    // Registrar un préstamo
    @Transactional
    public Prestamo registrar(Integer idUsuario, Integer idLibro) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el estudiante"));
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("No existe el libro"));

        if (libro.getEjemplaresDisponibles() == null || libro.getEjemplaresDisponibles() <= 0) {
            throw new IllegalStateException("No hay ejemplares disponibles para préstamo");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaLimite(LocalDate.now().plusDays(DIAS_PRESTAMO));
        prestamo.setEstado("ACTIVO");

        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    // Renovar un préstamo
    @Transactional
    public Prestamo renovar(Integer idPrestamo) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo"));
        if (!"ACTIVO".equals(prestamo.getEstado())) {
            throw new IllegalStateException("No es posible renovar un préstamo que no está activo");
        }
        prestamo.setFechaLimite(prestamo.getFechaLimite().plusDays(DIAS_PRESTAMO));
        return prestamoRepository.save(prestamo);
    }

    // Devolver un préstamo
    @Transactional
    public Prestamo finalizar(Integer idPrestamo) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo"));
        prestamo.setEstado("DEVUELTO");
        prestamo.setFechaDevolucion(LocalDate.now());

        Libro libro = prestamo.getLibro();
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }
}