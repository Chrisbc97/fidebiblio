package com.fidebiblio.repository;

import com.fidebiblio.domain.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Todas las notificaciones de un usuario
    List<Notificacion> findByUsuario_IdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);

    // Cantidad de notificaciones sin leer
    long countByUsuario_IdUsuarioAndLeidaFalse(Integer idUsuario);
}