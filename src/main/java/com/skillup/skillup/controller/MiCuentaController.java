package com.skillup.skillup.controller;

import com.skillup.skillup.model.Rol;
import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Seccion;
import com.skillup.skillup.repository.RolRepository;
import com.skillup.skillup.repository.CursoRepository;
import com.skillup.skillup.repository.SeccionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MiCuentaController {

    private final RolRepository rolRepository;
    private final CursoRepository cursoRepository;
    private final SeccionRepository seccionRepository;

    public MiCuentaController(RolRepository rolRepository, CursoRepository cursoRepository, SeccionRepository seccionRepository) {
        this.rolRepository = rolRepository;
        this.cursoRepository = cursoRepository;
        this.seccionRepository = seccionRepository;
    }

    @GetMapping("/usuarios/mi_cuenta")
    public String index(HttpSession session, Model model) {
        Long idUsuario = (Long) session.getAttribute("roles_sistema");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        Rol usuario = rolRepository.findById(idUsuario).orElse(null);
        List<Curso> cursos = cursoRepository.findByEstudianteId(idUsuario);
        List<Seccion> secciones = seccionRepository.findByUsuarioId(idUsuario);

        Map<String, String> contenidoSecciones = new HashMap<>();
        for (Seccion s : secciones) {
            contenidoSecciones.put(s.getSeccion(), s.getContenido());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("cursos", cursos);
        model.addAttribute("contenido_secciones", contenidoSecciones);

        return "usuarios/mi_cuenta"; // => src/main/resources/templates/usuarios/mi_cuenta.html
    }
}
