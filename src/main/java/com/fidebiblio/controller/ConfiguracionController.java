package com.fidebiblio.controller;

import com.fidebiblio.service.ConfiguracionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    // Listado de parámetros globales (solo Admin)
    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("configuraciones", configuracionService.getConfiguraciones());
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
}