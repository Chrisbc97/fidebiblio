package com.fidebiblio.repository;

import com.fidebiblio.domain.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Reservas activas de un estudiante
    @Query("SELECT r FROM Reserva r WHERE r.usuario.idUsuario = :idUsuario AND r.estado = :estado ORDER BY r.fechaReserva DESC")
    List<Reserva> buscarActivasPorUsuario(Integer idUsuario, String estado);

    // Se calcula la posición de la cola para la espera de un libro
    @Query("SELECT r FROM Reserva r WHERE r.libro.idLibro = :idLibro AND r.estado = :estado ORDER BY r.fechaReserva ASC")
    List<Reserva> buscarActivasPorLibro(Integer idLibro, String estado);
}