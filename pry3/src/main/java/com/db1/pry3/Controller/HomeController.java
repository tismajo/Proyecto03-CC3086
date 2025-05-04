package com.db1.pry3.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String mostrarBienvenida(Model model) {
        model.addAttribute("mensajeBienvenida", "¡Bienvenido! Selecciona un reporte para ver.");
        return "index";  
    }
}
