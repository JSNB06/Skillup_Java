package com.skillup.skillup.controller;


import com.skillup.skillup.model.CursoAdmin;
import com.skillup.skillup.repository.CursoAdminRepository;
import com.skillup.skillup.service.CursoAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CursosAdminController {

    @Autowired
    private CursoAdminRepository cursoAdminRepository;

    @Autowired
    private CursoAdminService cursoAdminService;


    @GetMapping("/cursosadmin")
    public String index(Model model) {
        List<CursoAdmin> cursos = cursoAdminRepository.findAll();
        model.addAttribute("cursos", cursos);
        return "administrador/cursosadmin";
    }

    @PostMapping("/cursosadmin/filtrar")
    public String filtrarCursos(
            @RequestParam(name = "nombre", required = false) String nombreCurso,
            @RequestParam(name = "estudiante", required = false) String nombreUsuario,
            @RequestParam(name = "identificacion", required = false) String identificacion,
            Model model
    ) {
        List<CursoAdmin> cursosFiltrados = cursoAdminService.filtrarCursos(
                nombreCurso, nombreUsuario, identificacion
        );

        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("oldNombre", nombreCurso);
        model.addAttribute("oldEstudiante", nombreUsuario);
        model.addAttribute("oldIdentificacion", identificacion);

        return "administrador/cursosadmin";
    }


}
