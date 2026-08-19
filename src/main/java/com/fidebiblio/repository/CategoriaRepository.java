package com.fidebiblio.repository;

import com.fidebiblio.domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrue();

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}