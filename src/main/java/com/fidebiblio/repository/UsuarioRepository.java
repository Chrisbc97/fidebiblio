package com.fidebiblio.repository;

import com.fidebiblio.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Se usa para validar el login por correo electrónico
    Optional<Usuario> findByCorreo(String correo);

    // Se usa para verificar duplicados al registrar
    boolean existsByIdentificacion(String identificacion);

    boolean existsByCorreo(String correo);

    List<Usuario> findByActivoTrue();

    // HU-02: búsqueda por nombre o correo (consulta ampliada)
    List<Usuario> findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCase(String nombre, String correo);
}
