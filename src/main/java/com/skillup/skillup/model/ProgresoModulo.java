package com.skillup.skillup.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progreso_modulo")
public class ProgresoModulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROGRESO")
    private Integer id;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "ID_MODULO")
    private Modulo modulo;

    @Column(name = "COMPLETADO")
    private Boolean completado = false;

    @Column(name = "FECHA_COMPLETADO")
    private LocalDateTime fechaCompletado;

    // Constructores
    public ProgresoModulo() {}

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

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Boolean getCompletado() {
        return completado;
    }

    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }
}