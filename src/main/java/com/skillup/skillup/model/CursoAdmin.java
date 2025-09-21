package com.skillup.skillup.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
public  class CursoAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSOS")
    private long idCurso;

    @Column(name = "NOMBRE_CURSO")
    private String nombreCurso;

    @Column(name = "NIVEL_DIFICULTAD")
    private String nivelDificultad;

   private Integer duracion;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENTIFICACION", referencedColumnName = "IDENTIFICACION")
    private Usuarios usuarios;



}