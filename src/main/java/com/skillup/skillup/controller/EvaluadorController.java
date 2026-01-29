package com.skillup.skillup.controller;



import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.UsuariosRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



@Controller
@RequestMapping("/evaluador")
public class EvaluadorController {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        // Obtener el ID del evaluador desde la sesión
        String evaluadorId = (String) session.getAttribute("roles_sistema");

        // Verificar que esté autenticado
        if (evaluadorId == null) {
            return "redirect:/login";
        }

        // Obtener datos del evaluador para mostrar en la vista
        Optional<Usuarios> evaluador = usuariosRepository.findById(evaluadorId);
        if (evaluador.isPresent()) {
            model.addAttribute("nombreEvaluador",
                    evaluador.get().getNombre() + " " + evaluador.get().getApellido1());
        }


        String nombreUsuario = (String) session.getAttribute("nombre_usuario");
        model.addAttribute("nombreUsuario", nombreUsuario);

        return "evaluador/inicio";
    }


    @GetMapping("/listaestudiantes")
    public String listaEstudiantes(HttpSession session, Model model) {
        // Verificar que esté autenticado
        String evaluadorId = (String) session.getAttribute("roles_sistema");

        if (evaluadorId == null) {
            return "redirect:/login";
        }

        // Obtener todos los estudiantes (rol 2)
        List<Usuarios> estudiantes = usuariosRepository.findByIdRol(2);
        model.addAttribute("estudiantes", estudiantes);

        return "evaluador/listaestudiantes";
    }
}