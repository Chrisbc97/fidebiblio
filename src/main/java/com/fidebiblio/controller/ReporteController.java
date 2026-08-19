package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.ReporteService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reporte")
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public ReporteController(ReporteService reporteService, UsuarioService usuarioService,
            NotificacionService notificacionService) {
        this.reporteService = reporteService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    // Panel de reportes y analíticas (bibliotecario o admin)
    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        model.addAttribute("totalLibros", reporteService.getTotalLibros());
        model.addAttribute("totalUsuarios", reporteService.getTotalUsuarios());
        model.addAttribute("totalPrestamos", reporteService.getTotalPrestamos());
        model.addAttribute("prestamosActivos", reporteService.getPrestamosActivos());
        model.addAttribute("multasPendientes", reporteService.getMultasPendientes());
        model.addAttribute("topLibros", reporteService.getTop5LibrosMasPrestados());
        agregarNotificaciones(model, principal);
        return "/reporte/listado";
    }

    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}