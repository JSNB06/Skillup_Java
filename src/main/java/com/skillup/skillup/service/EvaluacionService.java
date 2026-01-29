package com.skillup.skillup.service;

import com.skillup.skillup.Dto.EvaluacionFormDTO;
import com.skillup.skillup.model.*;
import com.skillup.skillup.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class EvaluacionService {

    private final CursoRepository cursoRepository;
    private final UsuariosRepository usuarioRepository;
    private final PreguntaEvaluacionRepository preguntaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final RespuestaEvaluacionRepository respuestaRepository;

    public EvaluacionService(CursoRepository cursoRepository,
                             UsuariosRepository usuarioRepository,
                             PreguntaEvaluacionRepository preguntaRepository,
                             EvaluacionRepository evaluacionRepository,
                             RespuestaEvaluacionRepository respuestaRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.preguntaRepository = preguntaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.respuestaRepository = respuestaRepository;
    }

    //  CREAR EVALUACIÓN (EVALUADOR)

    @Transactional
    public void crearEvaluacion(EvaluacionFormDTO formDTO) {
        Curso curso = cursoRepository.findById(formDTO.getIdCurso())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        for (EvaluacionFormDTO.PreguntaDTO p : formDTO.getPreguntas()) {
            PreguntaEvaluacion pregunta = new PreguntaEvaluacion();
            pregunta.setCurso(curso);
            pregunta.setPregunta(p.getPregunta());
            pregunta.setOpcionA(p.getOpcionA());
            pregunta.setOpcionB(p.getOpcionB());
            pregunta.setOpcionC(p.getOpcionC());
            pregunta.setOpcionD(p.getOpcionD());
            pregunta.setRespuestaCorrecta(p.getRespuestaCorrecta());
            pregunta.setPuntaje(10); // Puntaje por defecto
            pregunta.setActiva(true);
            preguntaRepository.save(pregunta);
        }
    }

    @Transactional(readOnly = true)
    public List<PreguntaEvaluacion> obtenerTodasLasPreguntas() {
        return preguntaRepository.findAll();
    }

    //  ESTUDIANTES

    // Obtener preguntas de un curso
    @Transactional(readOnly = true)
    public List<PreguntaEvaluacion> obtenerPreguntasPorCurso(Integer idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        return preguntaRepository.findByCurso(curso);
    }

    // Verificar si ya hizo la evaluación
    public boolean yaHizoEvaluacion(Integer idUsuario, Integer idCurso) {
        return evaluacionRepository.findByIdUsuarioAndCurso_Id(idUsuario, idCurso).isPresent();
    }

    @Transactional
    public Evaluacion guardarEvaluacionEstudiante(Integer idUsuario, Integer idCurso, Map<Integer, String> respuestas) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        //  Crear evaluación con estado PENDIENTE
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setIdUsuario(idUsuario);
        evaluacion.setCurso(curso);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setEstado("PENDIENTE"); //  Estado inicial

        //  NO calcular puntaje aún - lo hará el evaluador
        evaluacion.setPuntajeObtenido(BigDecimal.ZERO);
        evaluacion.setPuntajeTotal(BigDecimal.ZERO);
        evaluacion.setPorcentaje(BigDecimal.ZERO);

        evaluacion = evaluacionRepository.save(evaluacion);

        //  Guardar solo las respuestas seleccionadas (sin calificar aún)
        for (Map.Entry<Integer, String> entry : respuestas.entrySet()) {
            Integer idPregunta = entry.getKey();
            String respuestaSeleccionada = entry.getValue();

            PreguntaEvaluacion pregunta = preguntaRepository.findById(idPregunta)
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

            //  Guardar respuesta SIN evaluar (el evaluador lo hará)
            RespuestaEvaluacion respuesta = new RespuestaEvaluacion();
            respuesta.setEvaluacion(evaluacion);
            respuesta.setPregunta(pregunta);
            respuesta.setRespuestaSeleccionada(respuestaSeleccionada.trim().toUpperCase());
            respuesta.setPuntajeObtenido(0);
            respuestaRepository.save(respuesta);
        }

        return evaluacion;
    }


    // Obtener evaluaciones de un estudiante
    public List<Evaluacion> obtenerEvaluacionesEstudiante(Integer idUsuario) {
        return evaluacionRepository.findByIdUsuarioOrderByFechaEvaluacionDesc(idUsuario);
    }

    // EVALUADOR

    // Calificar Evaluación
    @Transactional
    public Evaluacion calificarEvaluacion(Integer idEvaluacion, Integer idEvaluador, String comentarios) {
        Evaluacion evaluacion = evaluacionRepository.findByIdWithRespuestas(idEvaluacion)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        //  Calcular puntaje revisando cada respuesta
        int puntajeObtenido = 0;
        int puntajeTotal = 0;

        for (RespuestaEvaluacion respuesta : evaluacion.getRespuestas()) {
            PreguntaEvaluacion pregunta = respuesta.getPregunta();
            String respuestaCorrecta = pregunta.getRespuestaCorrecta();
            String respuestaSeleccionada = respuesta.getRespuestaSeleccionada();

            boolean esCorrecta = false;

            if (respuestaSeleccionada != null && respuestaCorrecta != null) {
                String seleccionada = respuestaSeleccionada.trim().toUpperCase();
                String correcta = respuestaCorrecta.trim().toUpperCase();
                esCorrecta = seleccionada.equals(correcta);
            }

            int puntajePregunta = esCorrecta ? pregunta.getPuntaje() : 0;

            //  Actualizar respuesta
            respuesta.setEsCorrecta(esCorrecta);
            respuesta.setPuntajeObtenido(puntajePregunta);
            respuestaRepository.save(respuesta);

            puntajeObtenido += puntajePregunta;
            puntajeTotal += pregunta.getPuntaje();
        }

        //  Actualizar evaluación con puntajes
        evaluacion.setPuntajeObtenido(BigDecimal.valueOf(puntajeObtenido));
        evaluacion.setPuntajeTotal(BigDecimal.valueOf(puntajeTotal));

        if (puntajeTotal > 0) {
            BigDecimal porcentaje = BigDecimal.valueOf(puntajeObtenido)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(puntajeTotal), 2, RoundingMode.HALF_UP);
            evaluacion.setPorcentaje(porcentaje);

            //  Determinar estado
            if (porcentaje.doubleValue() >= 70) {
                evaluacion.setEstado("APROBADA");
            } else {
                evaluacion.setEstado("REPROBADA");
            }
        }

        evaluacion.setFechaRevision(LocalDateTime.now());
        evaluacion.setIdEvaluador(idEvaluador.toString());
        evaluacion.setComentarios(comentarios);

        return evaluacionRepository.save(evaluacion);
    }

    // Obtener evaluaciones por estado
    public List<Evaluacion> obtenerEvaluacionesPorEstado(String estado) {
        return evaluacionRepository.findByEstado(estado);
    }

    // Obtener evaluaciones pendientes
    public List<Evaluacion> obtenerEvaluacionesPendientes() {
        return evaluacionRepository.findByEstadoOrderByFechaEvaluacionAsc("PENDIENTE");
    }

    // Obtener evaluación con respuestas
    public Evaluacion obtenerEvaluacionConRespuestas(Integer idEvaluacion) {
        Evaluacion evaluacion = evaluacionRepository.findByIdWithRespuestas(idEvaluacion)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        // Si no cargó las respuestas con el query, cargarlas manualmente
        if (evaluacion.getRespuestas() == null || evaluacion.getRespuestas().isEmpty()) {
            List<RespuestaEvaluacion> respuestas = respuestaRepository.findByEvaluacion_Id(idEvaluacion);
            evaluacion.setRespuestas(respuestas.stream()
                    .collect(java.util.stream.Collectors.toSet()));
        }

        return evaluacion;
    }

    // Publicar resultado
    @Transactional
    public Evaluacion publicarResultado(Integer idEvaluacion, Integer idEvaluador, String estado, String comentarios) {
        Evaluacion evaluacion = evaluacionRepository.findById(idEvaluacion)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        evaluacion.setEstado(estado);
        evaluacion.setFechaRevision(LocalDateTime.now());
        evaluacion.setIdUsuario(idEvaluador);
        evaluacion.setComentarios(comentarios);

        return evaluacionRepository.save(evaluacion);
    }
}