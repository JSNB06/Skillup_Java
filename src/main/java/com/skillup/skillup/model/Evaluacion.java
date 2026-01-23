package com.skillup.skillup.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVALUACION")
    private Integer id;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "ID_CURSOS")
    private Curso curso;

    @Column(name = "FECHA_EVALUACION")
    private LocalDateTime fechaEvaluacion;

    @Column(name = "PUNTAJE_OBTENIDO", precision = 5, scale = 2)
    private BigDecimal puntajeObtenido;

    @Column(name = "PUNTAJE_TOTAL", precision = 5, scale = 2)
    private BigDecimal puntajeTotal;

    @Column(name = "PORCENTAJE", precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "ESTADO", length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "FECHA_REVISION")
    private LocalDateTime fechaRevision;

    @Column(name = "ID_EVALUADOR")
    private String idEvaluador;

    @Column(name = "COMENTARIOS", columnDefinition = "TEXT")
    private String comentarios;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL)
    private Set<RespuestaEvaluacion> respuestas = new HashSet<>();

    // Constructores
    public Evaluacion() {
        this.fechaEvaluacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public BigDecimal getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(BigDecimal puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public BigDecimal getPuntajeTotal() {
        return puntajeTotal;
    }

    public void setPuntajeTotal(BigDecimal puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(String idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Set<RespuestaEvaluacion> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(Set<RespuestaEvaluacion> respuestas) {
        this.respuestas = respuestas;
    }
}