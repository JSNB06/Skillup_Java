package com.skillup.skillup.controller;

import com.skillup.skillup.service.InternoClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class PruebaController {

    private final InternoClientService internoClient;

    public PruebaController(InternoClientService internoClient) {
        this.internoClient = internoClient;
    }

    // Vista para consumir los cursos internos (HTML)
    @GetMapping("/consumo-interno")
    public String consumoInterno(Model model) {
        model.addAttribute("cursos", internoClient.consumirMisCursos());
        return "consumo-interno";
    }

    // Nueva vista para mis cursos
    @GetMapping("/vista-cursos")
    public String vistaCursos(Model model) {
        List<String> cursos = internoClient.consumirMisCursos();
        model.addAttribute("cursos", cursos);
        return "vista-cursos"; // Thymeleaf buscará templates/vista-cursos.html
    }

    // Prueba con usuarios externos
    @GetMapping("/prueba-consumo")
    public String prueba(Model model) {
        String url = "https://jsonplaceholder.typicode.com/users";
        RestTemplate rt = new RestTemplate();
        Object[] usuarios = rt.getForObject(url, Object[].class);
        model.addAttribute("usuarios", usuarios);
        return "prueba";
    }
}