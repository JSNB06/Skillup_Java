package com.skillup.skillup.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/miscursos")
public class MisCursosApi {

    @GetMapping
    public List<String> listar() {
        return List.of("Liderazgo", "Comunicación", "Trabajo en equipo");
    }

    @GetMapping("/detalle")
    public List<Map<String,String>> listarDetalle() {
        return List.of(
                Map.of("nombre","Liderazgo", "duracion","4 semanas"),
                Map.of("nombre","Comunicación", "duracion","3 semanas")
        );
    }
}