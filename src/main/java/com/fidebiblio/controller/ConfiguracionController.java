package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.ConfiguracionService;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public ConfiguracionController(ConfiguracionService configuracionService, UsuarioService usuarioService,
            NotificacionService notificacionService) {
        this.configuracionService = configuracionService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    // Listado de parámetros globales (solo Admin)
    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        model.addAttribute("configuraciones", configuracionService.getConfiguraciones());
        agregarNotificaciones(model, principal);
        return "/configuracion/listado";
    }

    // Se actualiza un parámetro
    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idConfiguracion, @RequestParam String valor,
            RedirectAttributes redirectAttributes) {
        try {
            configuracionService.actualizar(idConfiguracion, valor);
            redirectAttributes.addFlashAttribute("todoOk", "Configuración actualizada satisfactoriamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/configuracion/listado";
    }
    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}