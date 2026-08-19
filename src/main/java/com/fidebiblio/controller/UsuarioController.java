package com.fidebiblio.controller;

import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public UsuarioController(UsuarioService usuarioService, NotificacionService notificacionService) {
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        agregarNotificaciones(model, principal);
        return "/usuario/listado";
    }

    // Consultar un usuario por nombre o correo
    @GetMapping("/buscar")
    public String buscar(@RequestParam String termino, Model model, Principal principal) {
        model.addAttribute("usuarios", usuarioService.buscar(termino));
        model.addAttribute("termino", termino);
        agregarNotificaciones(model, principal);
        return "/usuario/listado";
    }

    // Registrar o actualizar
    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getIdUsuario() == null) {
                usuarioService.registrar(usuario);
            } else {
                usuarioService.actualizar(usuario);
            }
            redirectAttributes.addFlashAttribute("todoOk", "Usuario guardado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

    // Desactivar usuario
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desactivar(idUsuario);
            redirectAttributes.addFlashAttribute("todoOk", "Usuario desactivado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        agregarNotificaciones(model, principal);
        return "/usuario/modifica";
    }

    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}