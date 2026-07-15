package com.fidebiblio.repository;

import com.fidebiblio.domain.Sugerencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SugerenciaRepository extends JpaRepository<Sugerencia, Integer> {

    List<Sugerencia> findAllByOrderByFechaCreacionDesc();

    // Detectar si ya existe una propuesta igual pendiente, para sumar voto
    @Query("SELECT s FROM Sugerencia s WHERE LOWER(s.titulo) = LOWER(:titulo) AND s.estado = :estado")
    Optional<Sugerencia> buscarPendientePorTitulo(String titulo, String estado);
}
