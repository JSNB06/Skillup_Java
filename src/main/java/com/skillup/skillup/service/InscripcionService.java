package com.skillup.skillup.service;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Inscripcion;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.CursoRepository;
import com.skillup.skillup.repository.InscripcionRepository;
import com.skillup.skillup.repository.ModuloRepository;
import com.skillup.skillup.repository.UsuariosRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class InscripcionService{

    @Autowired
    private InscripcionRepository inscripcionRepo;

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private UsuariosRepository usuariosRepo;

    @Autowired
    private ModuloRepository moduloRepo;

    public Integer inscribirEstudiante(Integer idCurso, String identificacionUsuario) {
        // Verificar si ya está inscrito
        if (inscripcionRepo.existsByCurso_IdAndUsuario_Identificacion(idCurso, identificacionUsuario)) {
            throw new RuntimeException("Ya existe una inscripción en este curso");
        }

        // Buscar curso y usuario
        Curso curso = cursoRepo.findById(idCurso).orElseThrow();
        Usuarios usuario = usuariosRepo
                .findByIdentificacion(identificacionUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        // Crear inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCurso(curso);
        inscripcion.setUsuario(usuario);
        inscripcionRepo.save(inscripcion);

        // Retornar el ID del primer módulo
        return moduloRepo.findByCurso_IdOrderByOrdenAsc(idCurso).get(0).getId();
    }


    public boolean estaInscrito(String identificacion, Integer idCurso) {
        return inscripcionRepo
                .existsByCurso_IdAndUsuario_Identificacion(idCurso, identificacion);
    }



    public List<Inscripcion> filtrarInscripciones(String nombreCurso, String nombreUsuario, String identificacion) {
        Specification<Inscripcion> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre del curso
            if (nombreCurso != null && !nombreCurso.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("curso").get("nombre")),
                        "%" + nombreCurso.toLowerCase() + "%"
                ));
            }

            // Filtro por nombre del usuario
            if (nombreUsuario != null && !nombreUsuario.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("usuario").get("nombre")),
                        "%" + nombreUsuario.toLowerCase() + "%"
                ));
            }

            // Filtro por identificación
            if (identificacion != null && !identificacion.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("usuario").get("identificacion"),
                        "%" + identificacion + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return inscripcionRepo.findAll(spec);
    }
}