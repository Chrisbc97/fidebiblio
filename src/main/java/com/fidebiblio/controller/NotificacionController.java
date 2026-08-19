package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/notificacion")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }
    @GetMapping("/marcar-leidas")
    @ResponseBody
    public String marcarLeidas(Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        notificacionService.marcarTodasLeidas(usuarioSesion.getIdUsuario());
        return "ok";
    }
}