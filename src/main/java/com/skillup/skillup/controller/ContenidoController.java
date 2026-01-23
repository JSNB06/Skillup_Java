package com.skillup.skillup.controller;

import com.skillup.skillup.model.Contenido;
import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.service.ContenidoService;
import com.skillup.skillup.service.ModuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administrador/contenidos")
public class ContenidoController {

    @Autowired
    private ContenidoService contenidoService;

    @Autowired
    private ModuloService moduloService;

    // Ver gestión de contenidos de un módulo
    @GetMapping("/modulo/{idModulo}")
    public String gestionarContenidos(@PathVariable Integer idModulo, Model model) {
        try {
            Modulo modulo = moduloService.obtenerModuloPorId(idModulo);
            List<Contenido> contenidos = contenidoService.obtenerContenidosPorModulo(idModulo);

            model.addAttribute("modulo", modulo);
            model.addAttribute("contenidos", contenidos);
            return "administrador/gestionarContenidos";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "redirect:/administrador/modulosCursos";
        }
    }

    @PostMapping("/crear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> crearContenido(
            @RequestParam Integer idModulo,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam(required = false) Integer orden) {

        Map<String, Object> response = new HashMap<>();

        try {
            contenidoService.crearContenido(idModulo, titulo, descripcion, orden);

            response.put("success", true);
            response.put("message", "Contenido creado exitosamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Actualizar contenido
    @PostMapping("/actualizar/{idContenido}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarContenido(
            @PathVariable Integer idContenido,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam(required = false) Integer orden) {

        Map<String, Object> response = new HashMap<>();

        try {
            Contenido contenido = contenidoService.actualizarContenido(idContenido, titulo, descripcion, orden);

            response.put("success", true);
            response.put("message", "Contenido actualizado exitosamente");
            response.put("contenido", contenido);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Eliminar contenido
    @DeleteMapping("/eliminar/{idContenido}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarContenido(@PathVariable Integer idContenido) {

        Map<String, Object> response = new HashMap<>();

        try {
            contenidoService.eliminarContenido(idContenido);

            response.put("success", true);
            response.put("message", "Contenido eliminado exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}