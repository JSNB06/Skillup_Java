package com.skillup.skillup.model;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "evaluaciones")
public class Evaluador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVALUACIONES")
    private Long id;

    @Column(name = "RESULTADO")
    private String resultado;

    @Column(name = "FECHA")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSOS", nullable = false)
    private CursoAdmin curso;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENTIFICACION", referencedColumnName = "IDENTIFICACION", nullable = false)
    private Usuarios estudiante;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENTIFICACION_EVALUADOR", referencedColumnName = "IDENTIFICACION", nullable = false)
    private Usuarios evaluador;

    @Column(name = "COMENTARIO")
    private String comentario;

    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getResultado() {
        return resultado;
    }
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public CursoAdmin getCurso() {
        return curso;
    }
    public void setCurso(CursoAdmin curso) {
        this.curso = curso;
    }

    public Usuarios getEstudiante() {
        return estudiante;
    }
    public void setEstudiante(Usuarios estudiante) {
        this.estudiante = estudiante;
    }

    public Usuarios getEvaluador() {
        return evaluador;
    }
    public void setEvaluador(Usuarios evaluador) {
        this.evaluador = evaluador;
    }

    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
