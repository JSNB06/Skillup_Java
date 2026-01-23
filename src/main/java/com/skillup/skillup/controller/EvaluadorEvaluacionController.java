package com.skillup.skillup.controller;

import com.skillup.skillup.model.Evaluacion;
import com.skillup.skillup.service.EvaluacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/evaluador/evaluaciones")
public class EvaluadorEvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluadorEvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    // Ver evaluaciones pendientes
    @GetMapping("/pendientes")
    public String verEvaluacionesPendientes(Model model) {
        List<Evaluacion> evaluaciones = evaluacionService.obtenerEvaluacionesPendientes();
        model.addAttribute("evaluaciones", evaluaciones);
        return "evaluador/evaluacionesPendientes";
    }

    // Ver detalle para revisar
    @GetMapping("/revisar/{idEvaluacion}")
    public String revisarEvaluacion(@PathVariable Integer idEvaluacion, Model model, RedirectAttributes redirectAttributes) {
        try {
            Evaluacion evaluacion = evaluacionService.obtenerEvaluacionConRespuestas(idEvaluacion);

            // Verificar que esté pendiente
            if (!"PENDIENTE".equals(evaluacion.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "Esta evaluación ya fue revisada");
                return "redirect:/evaluador/evaluaciones/pendientes";
            }

            model.addAttribute("evaluacion", evaluacion);
            return "evaluador/revisarEvaluacion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/evaluador/evaluaciones/pendientes";
        }
    }

    // Publicar resultado y Calificar automaticante
    @PostMapping("/publicar/{idEvaluacion}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> publicarResultado(
            @PathVariable Integer idEvaluacion,
            @RequestParam(required = false) String comentarios,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener ID del evaluador desde la sesión
            String identificacion = (String) session.getAttribute("roles_sistema");

            if (identificacion == null) {
                response.put("success", false);
                response.put("message", "No autenticado");
                return ResponseEntity.status(401).body(response);
            }

            Integer idEvaluador = Integer.parseInt(identificacion);

            //  CALIFICAR AUTOMÁTICAMENTE (calcula puntaje y determina APROBADA/REPROBADA)
            Evaluacion evaluacion = evaluacionService.calificarEvaluacion(
                    idEvaluacion,
                    idEvaluador,
                    comentarios != null ? comentarios : ""
            );

            response.put("success", true);
            response.put("message", "Evaluación calificada y publicada exitosamente");
            response.put("estado", evaluacion.getEstado());
            response.put("porcentaje", evaluacion.getPorcentaje());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Ver historial de evaluciones realizadas
    @GetMapping("/historial")
    public String historialEvaluaciones(Model model) {
        List<Evaluacion> aprobadas = evaluacionService.obtenerEvaluacionesPorEstado("APROBADA");
        List<Evaluacion> reprobadas = evaluacionService.obtenerEvaluacionesPorEstado("REPROBADA");

        model.addAttribute("aprobadas", aprobadas);
        model.addAttribute("reprobadas", reprobadas);

        return "evaluador/historialEvaluaciones";
    }


    // Ver a detalle las evaluaciones (Modo lectura)
    @GetMapping("/ver/{idEvaluacion}")
    public String verDetalleEvaluacion(@PathVariable Integer idEvaluacion,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            Evaluacion evaluacion = evaluacionService.obtenerEvaluacionConRespuestas(idEvaluacion);

            // Verificar que la evaluación ya fue revisada
            if ("PENDIENTE".equals(evaluacion.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "Esta evaluación aún no ha sido revisada");
                return "redirect:/evaluador/evaluaciones/pendientes";
            }

            model.addAttribute("evaluacion", evaluacion);
            model.addAttribute("soloLectura", true); // Indica que es solo lectura

            return "evaluador/detalleEvaluacion";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/evaluador/evaluaciones/historial";
        }
    }

}