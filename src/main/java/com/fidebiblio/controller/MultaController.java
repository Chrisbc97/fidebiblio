package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.MultaService;
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

    public MultaController(MultaService multaService, UsuarioService usuarioService) {
        this.multaService = multaService;
        this.usuarioService = usuarioService;
    }

    // Multas del usuario 
    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("multas", multaService.getMultasPorUsuario(usuarioSesion.getIdUsuario()));
        return "/multa/listado";
    }

    // Todas las multas pendientes (bibliotecario o admin)
    @GetMapping("/pendientes")
    public String pendientes(Model model) {
        model.addAttribute("multas", multaService.getMultasPendientes());
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
}