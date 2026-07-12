package com.fidebiblio.repository;

import com.fidebiblio.domain.Prestamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    // Historial de préstamos de los estudiantes, del más reciente al más antiguo
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.idUsuario = :idUsuario ORDER BY p.fechaPrestamo DESC")
    List<Prestamo> buscarHistorialPorUsuario(Integer idUsuario);

    // Listado general de préstamos activos (para vista del bibliotecario)
    @Query("SELECT p FROM Prestamo p WHERE p.estado = :estado ORDER BY p.fechaLimite ASC")
    List<Prestamo> buscarPorEstado(String estado);
}