package com.skillup.skillup.controller;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/administrador/cursos")
public class AdminGestionCursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ModuloService moduloService;

    //LISTAR LOS CURSOS
    @GetMapping("/listar")
    public String listarCursos(Model model) {
        List<Curso> cursos = cursoService.obtenerTodos();
        model.addAttribute("cursos", cursos);
        return "administrador/listarCursos";
    }

    // VER MODULOS POR CURSOS
    @GetMapping("/{idCurso}/modulos")
    public String verModulos(@PathVariable Integer idCurso, Model model) {
        try {
            Curso curso = cursoService.obtenerCursoEntity(idCurso);
            List<Modulo> modulos = moduloService.obtenerModulosPorCurso(idCurso);

            model.addAttribute("curso", curso);
            model.addAttribute("modulos", modulos);
            return "administrador/modulosCursos";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/administrador/listarCursos";
        }
    }
}