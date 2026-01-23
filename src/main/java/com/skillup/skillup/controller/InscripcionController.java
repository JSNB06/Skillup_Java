package com.skillup.skillup.controller;

import com.skillup.skillup.service.InscripcionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/estudiante/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @PostMapping("/inscribirse/{idCurso}")
    public String inscribirse(@PathVariable Integer idCurso,
                              HttpSession session,
                              Model model) {
        try {
            // Obtener identificación del usuario desde la sesión
            String identificacion = (String) session.getAttribute("roles_sistema");

            // Verificar que el usuario esté autenticado
            if (identificacion == null) {
                model.addAttribute("error", "Debes iniciar sesión para inscribirte");
                return "redirect:/login";
            }

            // Inscribir al estudiante
            inscripcionService.inscribirEstudiante(idCurso, identificacion);


            return "redirect:/estudiante/curso/" + idCurso;

        } catch (Exception e) {
            model.addAttribute("error", "Error al inscribirse: " + e.getMessage());
            return "redirect:/estudiante/cursos";
        }
    }
}

