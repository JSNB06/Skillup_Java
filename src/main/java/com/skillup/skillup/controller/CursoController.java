package com.skillup.skillup.controller;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Evaluacion;
import com.skillup.skillup.model.Inscripcion;
import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.repository.CursoRepository;
import com.skillup.skillup.repository.InscripcionRepository;
import com.skillup.skillup.repository.ProgresoModuloRepository;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.EvaluacionService;
import com.skillup.skillup.service.InscripcionService;
import com.skillup.skillup.service.ProgresoModuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estudiante")
public class CursoController {

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private ProgresoModuloService progresoService;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ProgresoModuloRepository progresoModuloRepository;

    @Autowired
    private EvaluacionService evaluacionService;

    @GetMapping("/cursos")
    public String listarCursos(Model model) {
        model.addAttribute("cursos", cursoService.obtenerTodos());
        return "estudiante/cursos";
    }

    @GetMapping("/cursos/{idCurso}")
    public String verCurso(@PathVariable Integer idCurso,
                           HttpSession session,
                           Model model) {
        try {
            // Obtener identificación del usuario
            String identificacion = (String) session.getAttribute("roles_sistema");

            // Obtener el curso
            Curso curso = cursoService.obtenerCursoConModulos(idCurso);

            // Verificar si el estudiante está inscrito
            boolean estaInscrito = false;
            if (identificacion != null) {
                estaInscrito = inscripcionService.estaInscrito(identificacion, idCurso);
            }

            // SI ESTÁ INSCRITO → Mostrar vista con progreso
            if (estaInscrito) {
                Integer idUsuario = Integer.parseInt(identificacion);

                // Obtener progreso
                List<ProgresoModulo> progresos = progresoService.obtenerProgresoPorUsuarioYCurso(idUsuario, idCurso);

                Set<Integer> modulosCompletadosIds = progresos.stream()
                        .filter(ProgresoModulo::getCompletado)
                        .map(p -> p.getModulo().getId())
                        .collect(Collectors.toSet());

                // Marcar módulos como completados
                if (curso.getModulos() != null) {
                    curso.getModulos().forEach(modulo -> {
                        modulo.setCompletado(modulosCompletadosIds.contains(modulo.getId()));
                    });
                }

                // Calcular estadísticas
                long totalModulos = curso.getModulos() != null ? curso.getModulos().size() : 0;
                long modulosCompletados = modulosCompletadosIds.size();
                double porcentajeAvance = totalModulos > 0
                        ? (modulosCompletados * 100.0) / totalModulos
                        : 0;

                boolean puedeHacerEvaluacion = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);

                // Agregar datos al modelo
                model.addAttribute("curso", curso);
                model.addAttribute("porcentajeAvance", Math.round(porcentajeAvance));
                model.addAttribute("puedeHacerEvaluacion", puedeHacerEvaluacion);
                model.addAttribute("totalModulos", totalModulos);
                model.addAttribute("modulosCompletados", modulosCompletados);

                // Retornar vista con progreso
                return "estudiante/curso-estudiante";
            }

            // SI NO ESTÁ INSCRITO → Mostrar vista previa
            model.addAttribute("curso", curso);
            return "estudiante/curso-detalle";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/estudiante/cursos";
        }
    }

    // ENDPOINT PARA VER EL PROGRESO EN LA SECCION DE "PROGRESO"
    @GetMapping("/progreso")
    public String verProgreso(HttpSession session, Model model) {
        String identificacion = (String) session.getAttribute("roles_sistema");
        if (identificacion == null) {
            return "redirect:/login";
        }

        Integer idUsuario = Integer.parseInt(identificacion);

        // Obtener cursos inscritos
        List<Inscripcion> inscripciones = inscripcionRepository.findByIdentificacion(idUsuario);

        // Lista para almacenar información de progreso
        List<Map<String, Object>> cursosProgreso = new ArrayList<>();

        int totalCursos = inscripciones.size();
        int cursosCompletados = 0;
        int cursosEnProgreso = 0;
        int evaluacionesAprobadas = 0;

        for (Inscripcion inscripcion : inscripciones) {
            Curso curso = inscripcion.getCurso();
            Map<String, Object> info = new HashMap<>();

            info.put("curso", curso);
            info.put("inscripcion", inscripcion);

            // Calcular progreso de módulos
            Long modulosCompletados = progresoModuloRepository.countModulosCompletadosByCurso(idUsuario, curso.getId());
            Long totalModulos = progresoModuloRepository.countTotalModulosByCurso(curso.getId());

            info.put("modulosCompletados", modulosCompletados);
            info.put("totalModulos", totalModulos);

            // Calcular porcentaje de progreso
            BigDecimal porcentajeProgreso = BigDecimal.ZERO;
            if (totalModulos > 0) {
                porcentajeProgreso = BigDecimal.valueOf(modulosCompletados)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalModulos), 2, RoundingMode.HALF_UP);
            }
            info.put("porcentajeProgreso", porcentajeProgreso);

            // Verificar si completó el curso
            boolean cursoCompleto = totalModulos > 0 && modulosCompletados.equals(totalModulos);
            info.put("cursoCompleto", cursoCompleto);

            if (cursoCompleto) {
                cursosCompletados++;
            } else if (modulosCompletados > 0) {
                cursosEnProgreso++;
            }

            // Verificar si tiene evaluación
            boolean yaHizoEvaluacion = evaluacionService.yaHizoEvaluacion(idUsuario, curso.getId());
            info.put("yaHizoEvaluacion", yaHizoEvaluacion);

            // Si ya hizo la evaluación, obtener resultado
            if (yaHizoEvaluacion) {
                List<Evaluacion> evaluaciones = evaluacionService.obtenerEvaluacionesEstudiante(idUsuario);
                Evaluacion evaluacion = evaluaciones.stream()
                        .filter(e -> e.getCurso().getId().equals(curso.getId()))
                        .findFirst()
                        .orElse(null);

                if (evaluacion != null) {
                    info.put("evaluacion", evaluacion);
                    if ("APROBADA".equals(evaluacion.getEstado())) {
                        evaluacionesAprobadas++;
                    }
                }
            }

            cursosProgreso.add(info);
        }

        // Calcular progreso general
        BigDecimal progresoGeneral = BigDecimal.ZERO;
        if (totalCursos > 0) {
            progresoGeneral = BigDecimal.valueOf(cursosCompletados)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCursos), 2, RoundingMode.HALF_UP);
        }

        model.addAttribute("cursosProgreso", cursosProgreso);
        model.addAttribute("totalCursos", totalCursos);
        model.addAttribute("cursosCompletados", cursosCompletados);
        model.addAttribute("cursosEnProgreso", cursosEnProgreso);
        model.addAttribute("evaluacionesAprobadas", evaluacionesAprobadas);
        model.addAttribute("progresoGeneral", progresoGeneral);
        model.addAttribute("title", "Mi Progreso");

        return "estudiante/progreso";
    }
}
