package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.ReservaService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    public ReservaController(ReservaService reservaService, UsuarioService usuarioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("reservas", reservaService.getReservasActivas(usuarioSesion.getIdUsuario()));
        return "/reserva/listado";
    }

    // Reservar un libro agotado
    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idLibro, Principal principal,
            RedirectAttributes redirectAttributes) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        try {
            var reserva = reservaService.reservar(usuarioSesion.getIdUsuario(), idLibro);
            redirectAttributes.addFlashAttribute("todoOk",
                    "Reserva confirmada. Te encuentras en la posición #" + reserva.getPosicion()
                            + " de la lista de espera");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reserva/listado";
    }

    // Cancelar reserva
    @PostMapping("/cancelar")
    public String cancelar(@RequestParam Integer idReserva, RedirectAttributes redirectAttributes) {
        try {
            reservaService.cancelar(idReserva);
            redirectAttributes.addFlashAttribute("todoOk", "Reserva cancelada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reserva/listado";
    }
}