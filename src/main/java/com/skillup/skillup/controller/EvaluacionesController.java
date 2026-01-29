package com.skillup.skillup.controller;

import com.skillup.skillup.Dto.EvaluacionFormDTO;
import com.skillup.skillup.model.PreguntaEvaluacion;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.security.Principal;
import java.util.List;


@Controller
public class EvaluacionesController {

    private final CursoService cursoService;
    private final EvaluacionService evaluacionService;

    public EvaluacionesController(CursoService cursoService, EvaluacionService evaluacionService) {
        this.cursoService = cursoService;
        this.evaluacionService = evaluacionService;
    }

    @GetMapping("/evaluador/formularioevaluacion")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cursos", cursoService.obtenerTodos());
        return "/evaluador/formularioevaluacion"; // nombre del HTML
    }

    @PostMapping("/evaluador/crearEvaluacion")
    public String crearEvaluacion(EvaluacionFormDTO formDTO) {
        evaluacionService.crearEvaluacion(formDTO);
        return "redirect:/evaluador/misevaluaciones";
    }

    @GetMapping("/evaluador/misevaluaciones")
    public String verMisEvaluaciones(Model model) {
        List<PreguntaEvaluacion> preguntas = evaluacionService.obtenerTodasLasPreguntas();
        model.addAttribute("preguntas", preguntas);
        return "evaluador/misevaluaciones";
    }

}