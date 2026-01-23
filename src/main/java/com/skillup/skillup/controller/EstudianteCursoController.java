package com.skillup.skillup.controller;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.ModuloService;
import com.skillup.skillup.service.ProgresoModuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estudiante/curso")
public class EstudianteCursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private ProgresoModuloService progresoService;

    @GetMapping("/{idCurso}")
    public String verCurso(@PathVariable Integer idCurso,
                           HttpSession session,
                           Model model) {
        try {
            // Obtener identificación del estudiante
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return "redirect:/login";
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            // Obtener curso completo con módulos
            Curso curso = cursoService.obtenerCursoConModulos(idCurso);

            // Obtener progreso del estudiante
            List<ProgresoModulo> progresos = progresoService.obtenerProgresoPorUsuarioYCurso(idUsuario, idCurso);

            // Crear un Set con los IDs de los módulos completados
            Set<Integer> modulosCompletadosIds = progresos.stream()
                    .filter(ProgresoModulo::getCompletado)
                    .map(p -> p.getModulo().getId())
                    .collect(Collectors.toSet());

            // Marcar módulos como completados en el modelo
            if (curso.getModulos() != null) {
                curso.getModulos().forEach(modulo -> {
                    // Agregar un atributo temporal para saber si está completado
                    modulo.setCompletado(modulosCompletadosIds.contains(modulo.getId()));
                });
            }

            // Calcular estadísticas
            long totalModulos = curso.getModulos() != null ? curso.getModulos().size() : 0;
            long modulosCompletados = modulosCompletadosIds.size();
            double porcentajeAvance = totalModulos > 0
                    ? (modulosCompletados * 100.0) / totalModulos
                    : 0;

            // Verificar si puede hacer la evaluación
            boolean puedeHacerEvaluacion = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);

            // Pasar datos a la vista
            model.addAttribute("curso", curso);
            model.addAttribute("porcentajeAvance", Math.round(porcentajeAvance));
            model.addAttribute("puedeHacerEvaluacion", puedeHacerEvaluacion);
            model.addAttribute("totalModulos", totalModulos);
            model.addAttribute("modulosCompletados", modulosCompletados);

            return "estudiante/curso-estudiante";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/estudiante/cursos";
        }
    }

    @PostMapping("/modulo/{idModulo}/completar")
    @ResponseBody
    public ResponseEntity<?> completarModulo(@PathVariable Integer idModulo,
                                             HttpSession session) {
        try {
            // Obtener ID del usuario desde la sesión
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "No autenticado"));
            }

            Integer idUsuario = Integer.parseInt(identificacion);

            // Marcar como completado
            progresoService.marcarModuloCompletado(idUsuario, idModulo);

            // Obtener el curso al que pertenece el módulo
            Integer idCurso = moduloService.obtenerCursoDelModulo(idModulo);

            // Calcular nuevo progreso
            List<ProgresoModulo> progresos = progresoService.obtenerProgresoPorUsuarioYCurso(idUsuario, idCurso);
            Curso curso = cursoService.obtenerCursoConModulos(idCurso);

            long totalModulos = curso.getModulos() != null ? curso.getModulos().size() : 0;
            long modulosCompletados = progresos.stream()
                    .filter(ProgresoModulo::getCompletado)
                    .count();

            double porcentajeAvance = totalModulos > 0
                    ? (modulosCompletados * 100.0) / totalModulos
                    : 0;

            boolean puedeHacerEvaluacion = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);

            // Respuesta exitosa con datos actualizados
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("porcentajeAvance", Math.round(porcentajeAvance));
            response.put("modulosCompletados", modulosCompletados);
            response.put("totalModulos", totalModulos);
            response.put("puedeHacerEvaluacion", puedeHacerEvaluacion);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

