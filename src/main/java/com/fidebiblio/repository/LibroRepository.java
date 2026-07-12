package com.fidebiblio.repository;

import com.fidebiblio.domain.Libro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer> {

    List<Libro> findByActivoTrue();

    // Buscar libro por título
    List<Libro> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);

    // Buscar libro por autor
    List<Libro> findByAutorContainingIgnoreCaseAndActivoTrue(String autor);

    // Buscar libros por categoría
    List<Libro> findByCategoria_IdCategoriaAndActivoTrue(Integer idCategoria);

    boolean existsByIsbn(String isbn);
}