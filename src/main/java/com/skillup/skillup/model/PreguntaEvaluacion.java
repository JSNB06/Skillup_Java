package com.skillup.skillup.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "preguntas_evaluacion")
public class PreguntaEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PREGUNTA")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_CURSOS")
    private Curso curso;

    @Column(name = "PREGUNTA", columnDefinition = "TEXT")
    private String pregunta;

    @Column(name = "OPCION_A", length = 500)
    private String opcionA;

    @Column(name = "OPCION_B", length = 500)
    private String opcionB;

    @Column(name = "OPCION_C", length = 500)
    private String opcionC;

    @Column(name = "OPCION_D", length = 500)
    private String opcionD;

    @Column(name = "RESPUESTA_CORRECTA", length = 1)
    private String respuestaCorrecta; // 'A', 'B', 'C', 'D'

    @Column(name = "PUNTAJE")
    private Integer puntaje = 10;

    @Column(name = "ACTIVA")
    private Boolean activa = true;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    // Constructores
    public PreguntaEvaluacion() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public void setOpcionA(String opcionA) {
        this.opcionA = opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }

    public void setOpcionB(String opcionB) {
        this.opcionB = opcionB;
    }

    public String getOpcionC() {
        return opcionC;
    }

    public void setOpcionC(String opcionC) {
        this.opcionC = opcionC;
    }

    public String getOpcionD() {
        return opcionD;
    }

    public void setOpcionD(String opcionD) {
        this.opcionD = opcionD;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}