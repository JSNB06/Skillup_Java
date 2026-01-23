package com.skillup.skillup.repository;

import com.skillup.skillup.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {

    // Buscar evaluaciones por ID de usuario (Integer)
    List<Evaluacion> findByIdUsuario(Integer idUsuario);

    // Buscar evaluaciones por estado
    List<Evaluacion> findByEstado(String estado);

    // Buscar evaluaciones por curso
    List<Evaluacion> findByCurso_Id(Integer idCurso);

    // Buscar evaluación específica de un usuario en un curso
    Optional<Evaluacion> findByIdUsuarioAndCurso_Id(Integer idUsuario, Integer idCurso);

    // Buscar evaluación con respuestas (para el evaluador)
    @Query("SELECT e FROM Evaluacion e LEFT JOIN FETCH e.respuestas WHERE e.id = :id")
    Optional<Evaluacion> findByIdWithRespuestas(@Param("id") Integer id);

    // Contar evaluaciones por estado
    Long countByEstado(String estado);

    // Buscar evaluaciones ordenadas por fecha
    List<Evaluacion> findByIdUsuarioOrderByFechaEvaluacionDesc(Integer idUsuario);

    // Buscar evaluaciones pendientes ordenadas
    List<Evaluacion> findByEstadoOrderByFechaEvaluacionAsc(String estado);
}