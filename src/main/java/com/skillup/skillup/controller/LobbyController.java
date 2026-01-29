package com.skillup.skillup.controller;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Evaluacion;
import com.skillup.skillup.model.Inscripcion;
import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.repository.EvaluacionRepository;
import com.skillup.skillup.repository.InscripcionRepository;
import com.skillup.skillup.repository.ProgresoModuloRepository;
import com.skillup.skillup.service.CursoService;
import com.skillup.skillup.service.EvaluacionService;
import com.skillup.skillup.service.ProgresoModuloService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/estudiante")
public class LobbyController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ProgresoModuloRepository progresoModuloRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private EvaluacionService evaluacionService;

    @Autowired
    private ProgresoModuloService progresoModuloService;

    @Autowired
    private CursoService cursoService;

    @GetMapping("/lobby")
    public String mostrarLobby(HttpSession session, Model model) {
        // Obtener identificación del usuario
        String identificacion = (String) session.getAttribute("roles_sistema");
        if (identificacion == null) {
            return "redirect:/login";
        }

        Integer idUsuario = Integer.parseInt(identificacion);

        // Datos para la estadistica del lobby
        // Total de cursos inscritos
        List<Inscripcion> inscripciones = inscripcionRepository.findByIdentificacion(idUsuario);
        int totalCursosActivos = inscripciones.size();

        // Evaluaciones disponibles (cursos completados sin evaluación hecha)
        int evaluacionesDisponibles = 0;
        for (Inscripcion inscripcion : inscripciones) {
            Integer idCurso = inscripcion.getCurso().getId();
            boolean puedeHacerEvaluacion = progresoModuloService.puedeHacerEvaluacion(idUsuario, idCurso);
            boolean yaHizoEvaluacion = evaluacionService.yaHizoEvaluacion(idUsuario, idCurso);

            if (puedeHacerEvaluacion && !yaHizoEvaluacion) {
                evaluacionesDisponibles++;
            }
        }

        // Progreso general (promedio de todos los cursos)
        BigDecimal progresoGeneralTotal = BigDecimal.ZERO;
        if (!inscripciones.isEmpty()) {
            for (Inscripcion inscripcion : inscripciones) {
                double porcentaje = progresoModuloService.calcularPorcentajeAvance(
                        idUsuario,
                        inscripcion.getCurso().getId()
                );
                progresoGeneralTotal = progresoGeneralTotal.add(BigDecimal.valueOf(porcentaje));
            }
            progresoGeneralTotal = progresoGeneralTotal.divide(
                    BigDecimal.valueOf(inscripciones.size()),
                    0,
                    RoundingMode.HALF_UP
            );
        }

        // Certificados obtenidos (evaluaciones aprobadas)
        List<Evaluacion> evaluaciones = evaluacionRepository.findByIdUsuario(idUsuario);
        long certificadosObtenidos = evaluaciones.stream()
                .filter(e -> "APROBADA".equals(e.getEstado()))
                .count();

        // CURSOS EN PROGRESO (Máximo 2 para mostrar)
        List<Map<String, Object>> cursosEnProgreso = new ArrayList<>();

        for (Inscripcion inscripcion : inscripciones) {
            Curso curso = inscripcion.getCurso();
            Integer idCurso = curso.getId();

            // Calcular progreso
            Long modulosCompletados = progresoModuloRepository.countModulosCompletadosByCurso(idUsuario, idCurso);
            Long totalModulos = progresoModuloRepository.countTotalModulosByCurso(idCurso);

            // Solo mostrar cursos que NO estén completados
            if (totalModulos > 0 && !modulosCompletados.equals(totalModulos)) {
                Map<String, Object> cursoInfo = new HashMap<>();

                BigDecimal porcentaje = BigDecimal.valueOf(modulosCompletados)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalModulos), 0, RoundingMode.HALF_UP);

                // Calcular tiempo estimado restante (10 min por módulo)
                long modulosRestantes = totalModulos - modulosCompletados;
                int minutosRestantes = (int) (modulosRestantes * 10);

                cursoInfo.put("curso", curso);
                cursoInfo.put("modulosCompletados", modulosCompletados);
                cursoInfo.put("totalModulos", totalModulos);
                cursoInfo.put("porcentajeProgreso", porcentaje);
                cursoInfo.put("minutosRestantes", minutosRestantes);
                cursoInfo.put("moduloActual", modulosCompletados + 1);

                // Determinar color de badge
                String colorBadge = "purple";
                if (porcentaje.intValue() >= 75) {
                    colorBadge = "blue";
                } else if (porcentaje.intValue() >= 50) {
                    colorBadge = "purple";
                }
                cursoInfo.put("colorBadge", colorBadge);

                cursosEnProgreso.add(cursoInfo);
            }
        }

        // Ordenar por porcentaje descendente y tomar solo los 2 primeros
        cursosEnProgreso.sort((a, b) -> {
            BigDecimal porcentajeA = (BigDecimal) a.get("porcentajeProgreso");
            BigDecimal porcentajeB = (BigDecimal) b.get("porcentajeProgreso");
            return porcentajeB.compareTo(porcentajeA);
        });

        if (cursosEnProgreso.size() > 2) {
            cursosEnProgreso = cursosEnProgreso.subList(0, 2);
        }

        //  ACTIVIDAD RECIENTE (Últimas 3 acciones)
        List<Map<String, Object>> actividadReciente = new ArrayList<>();

        // Obtener últimos módulos completados
        List<ProgresoModulo> progresos = progresoModuloRepository
                .findByIdUsuarioAndModulo_Curso_Id(idUsuario, null);

        // Filtrar solo completados y ordenar por fecha descendente
        List<ProgresoModulo> ultimosCompletados = new ArrayList<>();
        for (Inscripcion inscripcion : inscripciones) {
            List<ProgresoModulo> progresoCurso = progresoModuloRepository
                    .findByIdUsuarioAndModulo_Curso_Id(idUsuario, inscripcion.getCurso().getId());
            ultimosCompletados.addAll(progresoCurso.stream()
                    .filter(ProgresoModulo::getCompletado)
                    .collect(Collectors.toList()));
        }

        ultimosCompletados.sort((a, b) -> {
            LocalDateTime fechaA = a.getFechaCompletado();
            LocalDateTime fechaB = b.getFechaCompletado();
            if (fechaA == null) return 1;
            if (fechaB == null) return -1;
            return fechaB.compareTo(fechaA);
        });

        // Agregar módulos completados a actividad (máximo 2)
        int modulosAgregados = 0;
        for (ProgresoModulo progreso : ultimosCompletados) {
            if (modulosAgregados >= 2) break;

            Map<String, Object> actividad = new HashMap<>();
            actividad.put("tipo", "modulo_completado");
            actividad.put("titulo", "Módulo completado");
            actividad.put("descripcion", progreso.getModulo().getCurso().getNombre() + " - Módulo " + progreso.getModulo().getOrden());
            actividad.put("tiempo", calcularTiempoRelativo(progreso.getFechaCompletado()));
            actividad.put("icono", "check");
            actividad.put("colorIcono", "green");

            actividadReciente.add(actividad);
            modulosAgregados++;
        }

        // Agregar evaluaciones recientes
        evaluaciones.sort((a, b) -> {
            LocalDateTime fechaA = a.getFechaEvaluacion();
            LocalDateTime fechaB = b.getFechaEvaluacion();
            if (fechaA == null) return 1;
            if (fechaB == null) return -1;
            return fechaB.compareTo(fechaA);
        });

        for (Evaluacion evaluacion : evaluaciones) {
            if (actividadReciente.size() >= 3) break;

            Map<String, Object> actividad = new HashMap<>();
            actividad.put("tipo", "evaluacion");

            if ("PENDIENTE".equals(evaluacion.getEstado())) {
                actividad.put("titulo", "Evaluación enviada");
                actividad.put("descripcion", evaluacion.getCurso().getNombre() + " - En revisión");
                actividad.put("icono", "clipboard");
                actividad.put("colorIcono", "blue");
            } else if ("APROBADA".equals(evaluacion.getEstado())) {
                actividad.put("titulo", "Evaluación aprobada");
                actividad.put("descripcion", evaluacion.getCurso().getNombre() + " - Certificado disponible");
                actividad.put("icono", "check");
                actividad.put("colorIcono", "green");
            } else {
                actividad.put("titulo", "Evaluación reprobada");
                actividad.put("descripcion", evaluacion.getCurso().getNombre() + " - Revisar resultados");
                actividad.put("icono", "x");
                actividad.put("colorIcono", "red");
            }

            actividad.put("tiempo", calcularTiempoRelativo(evaluacion.getFechaEvaluacion()));
            actividadReciente.add(actividad);
        }

        // Agregar inscripciones recientes si no hay suficiente actividad
        if (actividadReciente.size() < 3) {
            for (Inscripcion inscripcion : inscripciones) {
                if (actividadReciente.size() >= 3) break;

                Map<String, Object> actividad = new HashMap<>();
                actividad.put("tipo", "inscripcion");
                actividad.put("titulo", "Inscripción nueva");
                actividad.put("descripcion", inscripcion.getCurso().getNombre());
                actividad.put("tiempo", "Hace " + inscripciones.indexOf(inscripcion) + " días"); // Aproximado
                actividad.put("icono", "plus");
                actividad.put("colorIcono", "purple");

                actividadReciente.add(actividad);
            }
        }


        model.addAttribute("totalCursosActivos", totalCursosActivos);
        model.addAttribute("evaluacionesDisponibles", evaluacionesDisponibles);
        model.addAttribute("progresoGeneral", progresoGeneralTotal);
        model.addAttribute("certificadosObtenidos", certificadosObtenidos);
        model.addAttribute("cursosEnProgreso", cursosEnProgreso);
        model.addAttribute("actividadReciente", actividadReciente);
        model.addAttribute("title", "Dashboard - SkillUp");

        // Obtener nombre del usuario para el saludo
        String nombreUsuario = (String) session.getAttribute("nombre");
        model.addAttribute("userName", nombreUsuario != null ? nombreUsuario : "Estudiante");

        return "estudiante/lobby";
    }

   // Calcular el tiempo relativo de una fecha hasta el momento
    private String calcularTiempoRelativo(LocalDateTime fecha) {
        if (fecha == null) return "Recientemente";

        LocalDateTime ahora = LocalDateTime.now();
        long minutos = ChronoUnit.MINUTES.between(fecha, ahora);

        if (minutos < 60) {
            return "Hace " + minutos + " min";
        }

        long horas = ChronoUnit.HOURS.between(fecha, ahora);
        if (horas < 24) {
            return "Hace " + horas + " h";
        }

        long dias = ChronoUnit.DAYS.between(fecha, ahora);
        if (dias == 1) {
            return "Hace 1 día";
        } else if (dias < 7) {
            return "Hace " + dias + " días";
        } else if (dias < 30) {
            long semanas = dias / 7;
            return "Hace " + semanas + (semanas == 1 ? " semana" : " semanas");
        } else {
            long meses = dias / 30;
            return "Hace " + meses + (meses == 1 ? " mes" : " meses");
        }
    }
}