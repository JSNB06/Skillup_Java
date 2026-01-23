package com.skillup.skillup.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "modulos")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MODULO")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_CURSOS")
    private Curso curso;

    @Column(name = "NOMBRE_MODULO")
    private String nombre;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ORDEN")
    private Integer orden;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<Contenido> contenidos = new ArrayList<>();

    @Transient
    private Boolean completado = false;


    public Modulo() {}

    public Boolean getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public List<Contenido> getContenidos() {
        return contenidos;
    }

    // ✅ RECIBE List EN VEZ DE Set
    public void setContenidos(List<Contenido> contenidos) {
        this.contenidos = contenidos;
    }
}
