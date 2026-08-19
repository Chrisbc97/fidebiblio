package com.fidebiblio.controller;

import com.fidebiblio.domain.Libro;
import com.fidebiblio.domain.Usuario;
import com.fidebiblio.service.CategoriaService;
import com.fidebiblio.service.LibroService;
import com.fidebiblio.service.NotificacionService;
import com.fidebiblio.service.UsuarioService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/libro")
public class LibroController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public LibroController(LibroService libroService, CategoriaService categoriaService,
            UsuarioService usuarioService, NotificacionService notificacionService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        var libros = libroService.getLibros(false);
        model.addAttribute("libros", libros);
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        model.addAttribute("totalLibros", libros.size());
        agregarNotificaciones(model, principal);
        return "/libro/listado";
    }

    // Registrar o actualizar
    @PostMapping("/guardar")
    public String guardar(@Valid Libro libro, @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        try {
            if (libro.getIdLibro() == null) {
                libroService.registrar(libro, imagenFile);
            } else {
                libroService.actualizar(libro, imagenFile);
            }
            redirectAttributes.addFlashAttribute("todoOk", "Libro guardado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/libro/listado";
    }

    // Eliminar o desactivar
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idLibro, RedirectAttributes redirectAttributes) {
        try {
            libroService.eliminar(idLibro);
            redirectAttributes.addFlashAttribute("todoOk", "Libro eliminado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/libro/listado";
    }

    // Actualiza el estado físico del libro (bibliotecario/admin)
    @PostMapping("/estado-fisico")
    public String actualizarEstadoFisico(@RequestParam Integer idLibro, @RequestParam String estadoFisico,
            RedirectAttributes redirectAttributes) {
        try {
            libroService.actualizarEstadoFisico(idLibro, estadoFisico);
            redirectAttributes.addFlashAttribute("todoOk", "Estado físico actualizado satisfactoriamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/libro/listado";
    }

    // Carga masiva de libros desde archivo CSV (bibliotecario/admin)
    @PostMapping("/cargar-csv")
    public String cargarCSV(@RequestParam MultipartFile archivoCsv, RedirectAttributes redirectAttributes) {
        if (archivoCsv.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un archivo CSV");
            return "redirect:/libro/listado";
        }
        try {
            List<String> resultado = libroService.cargarDesdeCSV(archivoCsv);
            redirectAttributes.addFlashAttribute("resultadoCarga", resultado);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo leer el archivo CSV");
        }
        return "redirect:/libro/listado";
    }

    @GetMapping("/modificar/{idLibro}")
    public String modificar(@PathVariable Integer idLibro, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        Optional<Libro> libroOpt = libroService.getLibro(idLibro);
        if (libroOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El libro no existe.");
            return "redirect:/libro/listado";
        }
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        agregarNotificaciones(model, principal);
        return "/libro/modifica";
    }

    private void agregarNotificaciones(Model model, Principal principal) {
        Usuario usuarioSesion = usuarioService.getUsuarioPorCorreo(principal.getName());
        model.addAttribute("notificacionesNoLeidas", notificacionService.contarNoLeidas(usuarioSesion.getIdUsuario()));
        model.addAttribute("notificacionesRecientes", notificacionService.getNotificaciones(usuarioSesion.getIdUsuario()));
    }
}
