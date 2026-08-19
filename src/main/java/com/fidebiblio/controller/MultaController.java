package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.MultaService;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/multa")
public class MultaController {

    private final MultaService multaService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public MultaController(MultaService multaService, UsuarioService usuarioService,
            NotificacionService notificacionService) {
        this.multaService = multaService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    // Multas del usuario 
    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("multas", multaService.getMultasPorUsuario(usuarioSesion.getIdUsuario()));
        agregarNotificaciones(model, principal);
        return "/multa/listado";
    }

    // Todas las multas pendientes (bibliotecario o admin)
    @GetMapping("/pendientes")
    public String pendientes(Model model, Principal principal) {
        model.addAttribute("multas", multaService.getMultasPendientes());
        agregarNotificaciones(model, principal);
        return "/multa/pendientes";
    }

    // Marcar una multa como pagada
    @PostMapping("/pagar")
    public String pagar(@RequestParam Integer idMulta, RedirectAttributes redirectAttributes) {
        try {
            multaService.marcarComoPagada(idMulta);
            redirectAttributes.addFlashAttribute("todoOk", "Multa marcada como pagada satisfactoriamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/multa/pendientes";
    }
    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}