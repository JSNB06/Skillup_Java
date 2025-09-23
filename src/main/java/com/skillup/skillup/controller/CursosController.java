package com.skillup.skillup.controller;

public package com.skillup.skillup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CursosController {

    // Página principal de cursos
    @GetMapping("/estudiante/cursos")
    public String index() {
        return "estudiante/cursos"; // Se busca en templates/estudiante/cursos.html
    }

    // Curso de Negociación
    @GetMapping("/estudiante/negociacion")
    public String negociacion() {
        return "estudiante/negociacion"; // templates/estudiante/negociacion.html
    }

    // Curso de Liderazgo
    @GetMapping("/estudiante/liderazgo")
    public String liderazgo() {
        return "estudiante/liderazgo";
    }

    // Curso de Trabajo en Equipo
    @GetMapping("/estudiante/trabajoequipo")
    public String trabajoEquipo() {
        return "estudiante/trabajoequipo";
    }

    // Curso de Comunicación
    @GetMapping("/estudiante/comunicacion")
    public String comunicacion() {
        return "estudiante/comunicacion";
    }
}
 {
    
}
