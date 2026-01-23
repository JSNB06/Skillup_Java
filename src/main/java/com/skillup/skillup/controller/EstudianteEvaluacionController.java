package com.skillup.skillup.controller;

import com.skillup.skillup.model.*;
import com.skillup.skillup.repository.InscripcionRepository;
import com.skillup.skillup.repository.ProgresoModuloRepository;
import com.skillup.skillup.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/estudiante/evaluacion")
public class EstudianteEvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private ProgresoModuloService progresoService;

    @Autowired
    private CursoService cursoService;
    
    @Autowired
    private InscripcionRepository inscripcionRepository;
    
    @Autowired
    private ProgresoModuloRepository progresoModuloRepository;

    @GetMapping("/evaluaciones")
    public String verEvaluaciones(HttpSession session, Model model) {
        String identificacion = (String) session.getAttribute("roles_sistema");
        if (identificacion == null) {
            return "redirect:/login";
        }

        Integer idUsuario = Integer.parseInt(identificacion);

        // Obtener cursos inscritos del estudiante
        List<Inscripcion> inscripciones = inscripcionRepository.findByIdentificacion(idUsuario);

        // Lista para almacenar información de evaluaciones
        List<Map<String, Object>> evaluacionesInfo = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            Curso curso = inscripcion.getCurso();
            Map<String, Object> info = new HashMap<>();

            info.put("curso", curso);
            info.put("inscripcion", inscripcion);

            // Verificar si completó todos los módulos usando tu servicio
            boolean cursoCompletado = progresoService.puedeHacerEvaluacion(idUsuario, curso.getId());

            // Obtener progreso de módulos
            Long modulosCompletados = progresoModuloRepository.countModulosCompletadosByCurso(idUsuario, curso.getId());
            Long totalModulos = progresoModuloRepository.countTotalModulosByCurso(curso.getId());

            info.put("cursoCompletado", cursoCompletado);
            info.put("modulosCompletados", modulosCompletados);
            info.put("totalModulos", totalModulos);

            // Verificar si ya hizo la evaluación
            boolean yaHizoEvaluacion = evaluacionService.yaHizoEvaluacion(idUsuario, curso.getId());
            info.put("yaHizoEvaluacion", yaHizoEvaluacion);

            // Si ya hizo la evaluación, obtener el resultado
            if (yaHizoEvaluacion) {
                List<Evaluacion> evaluaciones = evaluacionService.obtenerEvaluacionesEstudiante(idUsuario);
                Evaluacion evaluacion = evaluaciones.stream()
                        .filter(e -> e.getCurso().getId().equals(curso.getId()))
                        .findFirst()
                        .orElse(null);
                info.put("evaluacion", evaluacion);
            }

            evaluacionesInfo.add(info);
        }

        model.addAttribute("evaluacionesInfo", evaluacionesInfo);
        model.addAttribute("title", "Mis Evaluaciones");

        return "estudiante/evaluaciones";
    }

    @GetMapping("/realizar/{idCurso}")
    public String realizarEvaluacion(@PathVariable Integer idCurso,
                                     HttpSession session,
                                     Model model) {
        try {
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return "redirect:/login";
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            // Verificar que completó todos los módulos
            boolean puedeHacerEvaluacion = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);

            if (!puedeHacerEvaluacion) {
                model.addAttribute("error", "Debes completar todos los módulos antes de realizar la evaluación");
                return "redirect:/estudiante/curso/" + idCurso;
            }

            // Verificar si ya realizó la evaluación
            boolean yaHizoEvaluacion = evaluacionService.yaHizoEvaluacion(idUsuario, idCurso);

            if (yaHizoEvaluacion) {
                model.addAttribute("error", "Ya has realizado esta evaluación");
                return "redirect:/estudiante/evaluacion/misResultados";
            }

            // Obtener preguntas
            List<PreguntaEvaluacion> preguntas = evaluacionService.obtenerPreguntasPorCurso(idCurso);

            if (preguntas == null || preguntas.isEmpty()) {
                model.addAttribute("error", "Este curso no tiene evaluación disponible");
                return "redirect:/estudiante/curso/" + idCurso;
            }

            model.addAttribute("preguntas", preguntas);
            model.addAttribute("idCurso", idCurso);

            return "estudiante/realizarEvaluacion";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/estudiante/cursos";
        }
    }


    @PostMapping("/guardar/{idCurso}")
    @ResponseBody
    public ResponseEntity<?> guardarEvaluacion(@PathVariable Integer idCurso,
                                               @RequestBody Map<String, String> respuestasRaw,
                                               HttpSession session) {
        try {
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("success", false, "message", "No autenticado"));
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            // Verificar que puede hacer la evaluación
            boolean puedeHacerEvaluacion = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);

            if (!puedeHacerEvaluacion) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Debes completar todos los módulos primero"));
            }

            // Verificar si ya realizó la evaluación
            boolean yaHizoEvaluacion = evaluacionService.yaHizoEvaluacion(idUsuario, idCurso);

            if (yaHizoEvaluacion) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Ya has realizado esta evaluación"));
            }

            // Convertir respuestas: "pregunta_123" -> Map<Integer, String>
            Map<Integer, String> respuestas = new HashMap<>();

            for (Map.Entry<String, String> entry : respuestasRaw.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.startsWith("pregunta_")) {
                    Integer idPregunta = Integer.parseInt(key.replace("pregunta_", ""));
                    respuestas.put(idPregunta, value);
                }
            }

            // Guardar evaluación
            Evaluacion evaluacion = evaluacionService.guardarEvaluacionEstudiante(
                    idUsuario,
                    idCurso,
                    respuestas
            );

            // Respuesta a espera de revision
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", " Evaluación enviada exitosamente. Pendiente a revisión por el evaluador");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/misResultados")
    public String misResultados(HttpSession session, Model model) {
        try {
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return "redirect:/login";
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            List<Evaluacion> evaluaciones = evaluacionService.obtenerEvaluacionesEstudiante(idUsuario);

            model.addAttribute("evaluaciones", evaluaciones);

            return "estudiante/misResultados";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/estudiante/cursos";
        }
    }


    @GetMapping("/detalle/{idEvaluacion}")
    public String verDetalleEvaluacion(@PathVariable Integer idEvaluacion,
                                       HttpSession session,
                                       Model model) {
        try {
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return "redirect:/login";
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            // Obtener evaluación con respuestas
            Evaluacion evaluacion = evaluacionService.obtenerEvaluacionConRespuestas(idEvaluacion);

            // Verificar que pertenece al usuario
            if (!evaluacion.getIdUsuario().equals(idUsuario)) {
                model.addAttribute("error", "No tienes permiso para ver esta evaluación");
                return "redirect:/estudiante/evaluacion/misResultados";
            }

            model.addAttribute("evaluacion", evaluacion);

            return "estudiante/detalleEvaluacion";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/estudiante/evaluacion/misResultados";
        }
    }


}