package com.skillup.skillup.repository;

import com.skillup.skillup.model.CurriculumSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumSeccionRepository extends JpaRepository<CurriculumSeccion, Long> {

    // Trae todas las secciones de un usuario por su id
    List<CurriculumSeccion> findByUsuarioId(Long usuarioId);

    // Busca una sección específica (por usuario + título)
    Optional<CurriculumSeccion> findByUsuarioIdAndTitulo(Long usuarioId, String titulo);
}