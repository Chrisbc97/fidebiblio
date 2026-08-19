package com.fidebiblio.service;

import com.fidebiblio.domain.Notificacion;
import com.fidebiblio.domain.Usuario;
import com.fidebiblio.repository.NotificacionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> getNotificaciones(Integer idUsuario) {
        return notificacionRepository.findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(Integer idUsuario) {
        return notificacionRepository.countByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
    }

    // Crea una notificación nueva para un usuario.
    @Transactional
    public void crear(Usuario usuario, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacionRepository.save(notificacion);
    }

    // Marca todas las notificaciones de un usuario como leídas al abrir el icono
    @Transactional
    public void marcarTodasLeidas(Integer idUsuario) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario);
        for (Notificacion n : notificaciones) {
            if (!n.getLeida()) {
                n.setLeida(true);
                notificacionRepository.save(n);
            }
        }
    }
}