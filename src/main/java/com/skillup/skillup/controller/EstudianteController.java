package com.skillup.skillup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstudianteController {

    @GetMapping("/estudiante/liderazgo")
    public String liderazgo(Model model) {
        model.addAttribute("title", "SkillUp - Liderazgo");
        return "estudiante/liderazgo";
    }

    @GetMapping("/estudiante/comunicacion")
    public String comunicacion(Model model) {
        model.addAttribute("title", "SkillUp - Comunicación");
        return "estudiante/comunicacion";
    }

    @GetMapping("/estudiante/negociacion")
    public String negociacion(Model model) {
        model.addAttribute("title", "SkillUp - Negociación");
        return "estudiante/negociacion";
    }

    @GetMapping("/estudiante/trabajoequipo")
    public String trabajoEquipo(Model model) {
        model.addAttribute("title", "SkillUp - Trabajo en Equipo");
        return "estudiante/trabajoequipo";
    }

    @GetMapping("/estudiante/cursos")
    public String cursos(Model model) {
        model.addAttribute("title", "SkillUp - Cursos");
        return "estudiante/cursos";
    }

    @GetMapping("/estudiante/pago")
    public String pago(Model model) {
        model.addAttribute("title", "SkillUp - Pago");
        return "estudiante/pago";
    }
}
