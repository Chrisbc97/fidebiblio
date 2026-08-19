package com.fidebiblio.repository;

import com.fidebiblio.domain.Multa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Integer> {

    // Multas de un estudiante
    @Query("SELECT m FROM Multa m WHERE m.prestamo.usuario.idUsuario = :idUsuario ORDER BY m.fechaGeneracion DESC")
    List<Multa> buscarPorUsuario(Integer idUsuario);

    // Todas las multas pendientes de pago (bibliotecario)
    List<Multa> findByEstadoOrderByFechaGeneracionAsc(String estado);

    // Para no generar una multa duplicada sobre el mismo préstamo
    Optional<Multa> findByPrestamo_IdPrestamo(Integer idPrestamo);
}