package com.skillup.skillup.controller;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.repository.CursoRepository;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.InscripcionService;
import com.skillup.skillup.service.ProgresoModuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
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
}
