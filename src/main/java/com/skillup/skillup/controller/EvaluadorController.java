package com.skillup.skillup.controller;


import com.skillup.skillup.model.CursoAdmin;
import com.skillup.skillup.model.Evaluador;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.CursoAdminRepository;
import com.skillup.skillup.repository.EvaluadorRepository;
import com.skillup.skillup.repository.UsuariosRepository;
import com.skillup.skillup.service.EvaluadorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/evaluador")
public class EvaluadorController{

    @Autowired
    private EvaluadorService evaluadorService;

    @Autowired
    private EvaluadorRepository evaluadorRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private CursoAdminRepository cursoAdminRepository;

    @GetMapping("/inicio")
    public String inicio(){
        return "evaluador/inicio";
    }

    @GetMapping("/listaestudiantes")
    public String listaEstudiantes(Model model){
        model.addAttribute("estudiantes", usuariosRepository.findByIdRol(2));
        return "evaluador/listaestudiantes";
    }

    @GetMapping("/misevaluaciones")
    public String misEvaluaciones(HttpSession session, Model model){
        String evaluadorId = (String)session.getAttribute("roles_sistema");

        if(evaluadorId == null){
            return "redirect:/login";
        }

        List<Evaluador> evaluaciones = evaluadorService.getEvaluacionesPorEvaluador(evaluadorId);

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("evaluadorId", evaluadorId);

        return "evaluador/misevaluaciones";
    }

    @GetMapping("/formularioevaluacion")
    public String formularioEvaluacion(HttpSession session, Model model){
        String evaluadorId = (String)session.getAttribute("roles_sistema");
        if(evaluadorId == null){
            return "redirect:/login";
        }

        model.addAttribute("estudiantes",  usuariosRepository.findByIdRol(2));
        model.addAttribute("cursos", cursoAdminRepository.findByIdCursoIn(List.of(2L,3L,6L,8L)));
        model.addAttribute("evaluadorId", evaluadorId);
        return "evaluador/formularioevaluacion";
    }

    @PostMapping("/guardarevaluacion")
    public String guaradarEvaluacion(@RequestParam String estudiante,
                                     @RequestParam Integer curso,
                                     @RequestParam String resultado,
                                     @RequestParam String comentario,
                                     HttpSession session, RedirectAttributes redirectAttributes){

        String evaluadorId = (String)  session.getAttribute("roles_sistema");
        if(evaluadorId ==  null){
            return "redirect:/login";
        }

        Evaluador evaluador = new Evaluador();

        Usuarios estudianteEntity = usuariosRepository.findById(estudiante)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + estudiante));
        evaluador.setEstudiante(estudianteEntity);

        Usuarios evaluadorEntity = usuariosRepository.findById(evaluadorId)
                .orElseThrow(() -> new RuntimeException("Evaluador no encontrado con ID: " + evaluadorId));
        evaluador.setEvaluador(evaluadorEntity);

        CursoAdmin cursoEntity = cursoAdminRepository.findById(curso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + curso));
        evaluador.setCurso(cursoEntity);

        evaluador.setResultado(resultado);
        evaluador.setComentario(comentario);
        evaluador.setFecha(LocalDate.now());

        evaluadorRepository.save(evaluador);

        redirectAttributes.addFlashAttribute("mensaje", "Registro Exitoso");

        return "redirect:/evaluador/misevaluaciones";
    }

    @GetMapping("/eliminarevaluacion/{id}")
    public String eliminarEvaluacion(@PathVariable Long id, @RequestParam String evaluadorId){
        Optional<Evaluador> evaluacion = evaluadorRepository.findById(id);

        if (evaluacion.isEmpty() || !evaluacion.get().getEvaluador().getIdentificacion().equals(evaluadorId)){
            return  "redirect:/evaluador/misevaluaciones?evaluadorId=" + evaluadorId;
        }

        evaluadorRepository.deleteById(id);
        return "redirect:/evaluador/misevaluaciones?evaluadorId=" + evaluadorId;

    }

    @GetMapping("/editarevaluacion/{id}")
    public String editarEvaluacion(@PathVariable Long id, @RequestParam String evaluadorId, Model model){
        Optional<Evaluador> evaluacion = evaluadorRepository.findById(id);

        if (evaluacion.isEmpty() || !evaluacion.get().getEvaluador().getIdentificacion().equals(evaluadorId)){
            return "redirect:/evaluador/misevaluaciones?evaluadorId=" + evaluadorId;
        }

        model.addAttribute("evaluacion", evaluacion.get());
        model.addAttribute("evaluadorId", evaluadorId);
        return "evaluador/editarevaluacion";
    }

    @PostMapping("/actualizarevaluacion")
    public String actualizarEvaluacion(@RequestParam Long id,
                                       @RequestParam String resultado,
                                       @RequestParam String comentario,
                                       @RequestParam String evaluadorId){

        Optional<Evaluador> evaluacion = evaluadorRepository.findById(id);

        if(evaluacion.isEmpty() || !evaluacion.get().getEvaluador().getIdentificacion().equals(evaluadorId)){
            return "redirect:/evaluador/misevaluaciones?evaluadorId=" + evaluadorId;
        }

        Evaluador evaluadorEntity = evaluacion.get();
        evaluadorEntity.setResultado(resultado);
        evaluadorEntity.setComentario(comentario);
        evaluadorEntity.setFecha(LocalDate.now());

        evaluadorRepository.save(evaluadorEntity);

        return "redirect:/evaluador/misevaluaciones?evaluadorId=" + evaluadorId;
    }
}