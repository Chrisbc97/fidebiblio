package com.fidebiblio.service;

import com.fidebiblio.domain.Libro;
import com.fidebiblio.domain.Reserva;
import com.fidebiblio.domain.Usuario;
import com.fidebiblio.repository.LibroRepository;
import com.fidebiblio.repository.ReservaRepository;
import com.fidebiblio.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    // Límite máximo de reservas activas por estudiante
    private static final int LIMITE_RESERVAS = 3;

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository,
            LibroRepository libroRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<Reserva> getReservasActivas(Integer idUsuario) {
        return reservaRepository.buscarActivasPorUsuario(idUsuario, "ACTIVA");
    }

    // Reservar un libro agotado
    @Transactional
    public Reserva reservar(Integer idUsuario, Integer idLibro) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No existe el estudiante"));
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("No existe el libro"));

        // Si hay ejemplares disponibles, no se puede reservar 
        if (libro.getEjemplaresDisponibles() != null && libro.getEjemplaresDisponibles() > 0) {
            throw new IllegalStateException("El libro tiene ejemplares disponibles, use préstamo directo");
        }

        long reservasActivas = reservaRepository.buscarActivasPorUsuario(idUsuario, "ACTIVA").size();
        if (reservasActivas >= LIMITE_RESERVAS) {
            throw new IllegalStateException(
                    "Ha alcanzado el tope máximo de " + LIMITE_RESERVAS + " reservas simultáneas permitidas");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLibro(libro);
        reserva.setEstado("ACTIVA");
        long posicion = reservaRepository.buscarActivasPorLibro(idLibro, "ACTIVA").size() + 1;
        reserva.setPosicion((int) posicion);

        return reservaRepository.save(reserva);
    }

    // Cancelar una reserva y reordenar la cola
    @Transactional
    public void cancelar(Integer idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva"));
        Integer idLibro = reserva.getLibro().getIdLibro();
        int posicionCancelada = reserva.getPosicion() != null ? reserva.getPosicion() : 0;

        reserva.setEstado("CANCELADA");
        reservaRepository.save(reserva);

        // Reordena las posiciones de los usuarios que quedaban antes en la cola
        List<Reserva> siguientes = reservaRepository.buscarActivasPorLibro(idLibro, "ACTIVA");
        for (Reserva r : siguientes) {
            if (r.getPosicion() != null && r.getPosicion() > posicionCancelada) {
                r.setPosicion(r.getPosicion() - 1);
                reservaRepository.save(r);
            }
        }
    }
}
