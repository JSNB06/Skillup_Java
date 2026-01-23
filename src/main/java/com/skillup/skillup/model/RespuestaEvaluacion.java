package com.skillup.skillup.model;

import jakarta.persistence.*;

@Entity
@Table(name = "respuestas_evaluacion")
public class RespuestaEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESPUESTA")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_EVALUACION")
    private Evaluacion evaluacion;

    @ManyToOne
    @JoinColumn(name = "ID_PREGUNTA")
    private PreguntaEvaluacion pregunta;

    @Column(name = "RESPUESTA_SELECCIONADA", length = 1)
    private String respuestaSeleccionada;

    @Column(name = "ES_CORRECTA")
    private Boolean esCorrecta;

    @Column(name = "PUNTAJE_OBTENIDO")
    private Integer puntajeObtenido;

    // Constructores
    public RespuestaEvaluacion() {}

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Evaluacion evaluacion) {
        this.evaluacion = evaluacion;
    }

    public PreguntaEvaluacion getPregunta() {
        return pregunta;
    }

    public void setPregunta(PreguntaEvaluacion pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuestaSeleccionada() {
        return respuestaSeleccionada;
    }

    public void setRespuestaSeleccionada(String respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    public Boolean getEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(Boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public Integer getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(Integer puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }
}