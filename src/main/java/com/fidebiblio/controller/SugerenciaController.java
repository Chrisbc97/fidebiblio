package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.SugerenciaService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sugerencia")
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public SugerenciaController(SugerenciaService sugerenciaService, UsuarioService usuarioService,
            NotificacionService notificacionService) {
        this.sugerenciaService = sugerenciaService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        model.addAttribute("sugerencias", sugerenciaService.getSugerencias());
        agregarNotificaciones(model, principal);
        return "/sugerencia/listado";
    }

    // Proponer libro o sumar voto si ya existe una igual pendiente
    @PostMapping("/guardar")
    public String guardar(@RequestParam String titulo, @RequestParam(required = false) String autor,
            Principal principal, RedirectAttributes redirectAttributes) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        try {
            sugerenciaService.proponer(usuarioSesion.getIdUsuario(), titulo, autor);
            redirectAttributes.addFlashAttribute("todoOk",
                    "¡Gracias por tu colaboración! Tu sugerencia fue enviada al equipo de la biblioteca");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sugerencia/listado";
    }

    // Aprobar o rechazar (solamente bibliotecario)
    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Integer idSugerencia, @RequestParam String estado,
            RedirectAttributes redirectAttributes) {
        try {
            sugerenciaService.cambiarEstado(idSugerencia, estado);
            redirectAttributes.addFlashAttribute("todoOk", "Estado de la sugerencia actualizado");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sugerencia/listado";
    }

    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}