package com.skillup.skillup.controller;

import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.service.ProgresoModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/estudiante/progreso")
public class ProgresoController {

    @Autowired
    private ProgresoModuloService progresoService;

    // Marcar módulo como completado
    @PostMapping("/completar/{idModulo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completarModulo(
            @PathVariable Integer idModulo,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer idUsuario = Integer.parseInt(authentication.getName());

            ProgresoModulo progreso = progresoService.marcarModuloCompletado(idUsuario, idModulo);

            response.put("success", true);
            response.put("message", "Módulo completado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Verificar si puede hacer la evaluación
    @GetMapping("/puede-evaluar/{idCurso}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> puedeHacerEvaluacion(
            @PathVariable Integer idCurso,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer idUsuario = Integer.parseInt(authentication.getName());

            boolean puede = progresoService.puedeHacerEvaluacion(idUsuario, idCurso);
            double porcentaje = progresoService.calcularPorcentajeAvance(idUsuario, idCurso);

            response.put("success", true);
            response.put("puedeHacerEvaluacion", puede);
            response.put("porcentajeAvance", porcentaje);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}