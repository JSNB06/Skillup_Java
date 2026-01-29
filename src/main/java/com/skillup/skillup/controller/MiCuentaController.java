package com.skillup.skillup.controller;

import com.skillup.skillup.model.Evaluacion;
import com.skillup.skillup.model.Inscripcion;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.EvaluacionRepository;
import com.skillup.skillup.repository.InscripcionRepository;
import com.skillup.skillup.repository.ProgresoModuloRepository;
import com.skillup.skillup.repository.UsuariosRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/estudiante/mi-cuenta")
public class MiCuentaController {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ProgresoModuloRepository progresoModuloRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @GetMapping
    public String verMiCuenta(HttpSession session, Model model) {
        String identificacion = (String) session.getAttribute("roles_sistema");
        if (identificacion == null) {
            return "redirect:/login";
        }

        Integer idUsuario = Integer.parseInt(identificacion);

        // Obtener usuario
        Usuarios usuario = usuariosRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ========== ESTADÍSTICAS DEL USUARIO ==========

        // Total de inscripciones
        List<Inscripcion> inscripciones = inscripcionRepository.findByIdentificacion(idUsuario);
        int totalCursos = inscripciones.size();

        // Módulos completados totales
        long modulosCompletadosTotal = progresoModuloRepository
                .countByIdUsuarioAndCompletado(idUsuario, true);

        // Evaluaciones
        List<Evaluacion> evaluaciones = evaluacionRepository.findByIdUsuario(idUsuario);
        long evaluacionesAprobadas = evaluaciones.stream()
                .filter(e -> "APROBADA".equals(e.getEstado()))
                .count();
        long evaluacionesPendientes = evaluaciones.stream()
                .filter(e -> "PENDIENTE".equals(e.getEstado()))
                .count();

        // Calcular fecha de registro (aproximada)
        String fechaRegistro = "Miembro desde 2024"; // Puedes ajustar según tu BD

        // ========== ACTIVIDAD RECIENTE ==========
        List<Map<String, Object>> actividadReciente = new ArrayList<>();

        // Últimas evaluaciones
        evaluaciones.sort((a, b) -> {
            if (a.getFechaEvaluacion() == null) return 1;
            if (b.getFechaEvaluacion() == null) return -1;
            return b.getFechaEvaluacion().compareTo(a.getFechaEvaluacion());
        });

        for (Evaluacion eval : evaluaciones.subList(0, Math.min(5, evaluaciones.size()))) {
            Map<String, Object> actividad = new HashMap<>();
            actividad.put("tipo", "evaluacion");
            actividad.put("titulo", eval.getCurso().getNombre());
            actividad.put("descripcion", "Evaluación " + eval.getEstado().toLowerCase());
            actividad.put("fecha", formatearFecha(eval.getFechaEvaluacion()));
            actividad.put("icono", eval.getEstado().equals("APROBADA") ? "check" : "clock");
            actividad.put("color", eval.getEstado().equals("APROBADA") ? "green" : "blue");
            actividadReciente.add(actividad);
        }

        // ========== AGREGAR AL MODELO ==========
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalCursos", totalCursos);
        model.addAttribute("modulosCompletados", modulosCompletadosTotal);
        model.addAttribute("evaluacionesAprobadas", evaluacionesAprobadas);
        model.addAttribute("evaluacionesPendientes", evaluacionesPendientes);
        model.addAttribute("fechaRegistro", fechaRegistro);
        model.addAttribute("actividadReciente", actividadReciente);
        model.addAttribute("title", "Mi Cuenta - SkillUp");

        return "estudiante/mi-cuenta";
    }

    // ========== ACTUALIZAR INFORMACIÓN PERSONAL ==========
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellido1,
            @RequestParam String apellido2,
            @RequestParam String correo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String identificacion = (String) session.getAttribute("roles_sistema");
            Integer idUsuario = Integer.parseInt(identificacion);

            Usuarios usuario = usuariosRepository.findByIdentificacion(identificacion)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar datos
            usuario.setNombre(nombre);
            usuario.setApellido1(apellido1);
            usuario.setApellido2(apellido2);
            usuario.setCorreo(correo);

            usuariosRepository.save(usuario);

            // Actualizar nombre en sesión
            session.setAttribute("nombre", nombre);

            redirectAttributes.addFlashAttribute("success", "✅ Perfil actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/estudiante/mi-cuenta";
    }

    // ========== CAMBIAR CONTRASEÑA (SIN ENCRIPTACIÓN) ==========
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmar,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String identificacion = (String) session.getAttribute("roles_sistema");
            Integer idUsuario = Integer.parseInt(identificacion);

            Usuarios usuario = usuariosRepository.findByIdentificacion(identificacion)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // ✅ Verificar contraseña actual (sin encriptación - comparación directa)
            if (!passwordActual.equals(usuario.getContrasena())) {
                redirectAttributes.addFlashAttribute("errorPassword", "❌ La contraseña actual es incorrecta");
                return "redirect:/estudiante/mi-cuenta";
            }

            // ✅ Verificar que las nuevas contraseñas coincidan
            if (!passwordNueva.equals(passwordConfirmar)) {
                redirectAttributes.addFlashAttribute("errorPassword", "❌ Las contraseñas nuevas no coinciden");
                return "redirect:/estudiante/mi-cuenta";
            }

            // ✅ Validar longitud mínima
            if (passwordNueva.length() < 6) {
                redirectAttributes.addFlashAttribute("errorPassword", "❌ La contraseña debe tener al menos 6 caracteres");
                return "redirect:/estudiante/mi-cuenta";
            }

            // ✅ Validar que la nueva contraseña sea diferente a la actual
            if (passwordNueva.equals(passwordActual)) {
                redirectAttributes.addFlashAttribute("errorPassword", "⚠️ La nueva contraseña debe ser diferente a la actual");
                return "redirect:/estudiante/mi-cuenta";
            }

            // ✅ Actualizar contraseña (sin encriptar - texto plano)
            usuario.setContrasena(passwordNueva);
            usuariosRepository.save(usuario);

            redirectAttributes.addFlashAttribute("successPassword", "✅ Contraseña actualizada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorPassword", "❌ Error al cambiar contraseña: " + e.getMessage());
        }

        return "redirect:/estudiante/mi-cuenta";
    }

    // ========== MÉTODO AUXILIAR PARA FORMATEAR FECHAS ==========
    private String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) return "Fecha desconocida";

        LocalDateTime ahora = LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(fecha, ahora);

        if (dias == 0) {
            return "Hoy";
        } else if (dias == 1) {
            return "Ayer";
        } else if (dias < 7) {
            return "Hace " + dias + " días";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return fecha.format(formatter);
        }
    }
}