package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.LibroService;
import com.fidebiblio.service.PrestamoService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prestamo")
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final UsuarioService usuarioService;
    private final LibroService libroService;

    public PrestamoController(PrestamoService prestamoService, UsuarioService usuarioService,
            LibroService libroService) {
        this.prestamoService = prestamoService;
        this.usuarioService = usuarioService;
        this.libroService = libroService;
    }

    // Listado de préstamos activos para el bibliotecario
    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("prestamos", prestamoService.getPrestamosActivos());
        model.addAttribute("usuarios", usuarioService.getUsuarios(true));
        model.addAttribute("libros", libroService.getLibros(true));
        return "/prestamo/listado";
    }

    // Historial de préstamos del usuario 
    @GetMapping("/historial")
    public String historial(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("prestamos", prestamoService.getHistorial(usuarioSesion.getIdUsuario()));
        return "/prestamo/historial";
    }

    // Registrar préstamo.
    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idUsuario, @RequestParam Integer idLibro,
            @RequestParam(required = false, defaultValue = "/prestamo/listado") String redirectTo,
            RedirectAttributes redirectAttributes) {
        try {
            prestamoService.registrar(idUsuario, idLibro);
            redirectAttributes.addFlashAttribute("todoOk", "Préstamo registrado satisfactoriamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + redirectTo;
    }

    // Renovar préstamo
    @PostMapping("/renovar")
    public String renovar(@RequestParam Integer idPrestamo, RedirectAttributes redirectAttributes) {
        try {
            prestamoService.renovar(idPrestamo);
            redirectAttributes.addFlashAttribute("todoOk", "Préstamo renovado satisfactoriamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamo/listado";
    }

    // Finalizar o devolver préstamo
    @PostMapping("/finalizar")
    public String finalizar(@RequestParam Integer idPrestamo, RedirectAttributes redirectAttributes) {
        try {
            prestamoService.finalizar(idPrestamo);
            redirectAttributes.addFlashAttribute("todoOk", "Préstamo finalizado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamo/listado";
    }
}