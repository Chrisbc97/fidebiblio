package com.fidebiblio.controller;

import com.fidebiblio.service.CategoriaService;
import com.fidebiblio.service.LibroService;
import com.fidebiblio.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public IndexController(LibroService libroService, CategoriaService categoriaService,
            UsuarioService usuarioService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    // Catálogo principal, con búsqueda por título, autor, categoría 
    @GetMapping("/")
    public String index(@RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Integer idCategoria,
            Model model, Principal principal) {

        if (titulo != null && !titulo.isBlank()) {
            model.addAttribute("libros", libroService.buscarPorTitulo(titulo));
        } else if (autor != null && !autor.isBlank()) {
            model.addAttribute("libros", libroService.buscarPorAutor(autor));
        } else if (idCategoria != null) {
            model.addAttribute("libros", libroService.buscarPorCategoria(idCategoria));
        } else {
            model.addAttribute("libros", libroService.getLibros(true));
        }

        model.addAttribute("categorias", categoriaService.getCategorias(true));
        model.addAttribute("idUsuarioSesion",
                usuarioService.getUsuarioPorCorreo(principal.getName()).getIdUsuario());
        return "/index";
    }
}