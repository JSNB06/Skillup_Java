package com.skillup.skillup.repository;

import com.skillup.skillup.model.Evaluador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluadorRepository  extends JpaRepository<Evaluador, Long> {

    @Query("SELECT e FROM Evaluador e " +
            "JOIN FETCH e.estudiante est " +
            "LEFT JOIN FETCH e.curso c " +
            "WHERE e.evaluador.identificacion = :idEvaluador " +
            "ORDER BY e.fecha DESC")

    List<Evaluador> findEvaluacionesDelEvaluador(@Param("idEvaluador") String idEvaluador);
}
