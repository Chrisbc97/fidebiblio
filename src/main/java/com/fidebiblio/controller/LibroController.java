package com.fidebiblio.controller;

import com.fidebiblio.domain.Libro;
import com.fidebiblio.service.CategoriaService;
import com.fidebiblio.service.LibroService;
import jakarta.validation.Valid;
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

    public LibroController(LibroService libroService, CategoriaService categoriaService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var libros = libroService.getLibros(false);
        model.addAttribute("libros", libros);
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        model.addAttribute("totalLibros", libros.size());
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

    @GetMapping("/modificar/{idLibro}")
    public String modificar(@PathVariable Integer idLibro, Model model, RedirectAttributes redirectAttributes) {
        Optional<Libro> libroOpt = libroService.getLibro(idLibro);
        if (libroOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El libro no existe.");
            return "redirect:/libro/listado";
        }
        model.addAttribute("libro", libroOpt.get());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "/libro/modifica";
    }
}