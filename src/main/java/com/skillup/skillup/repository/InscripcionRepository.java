package com.skillup.skillup.repository;


import com.skillup.skillup.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer>, JpaSpecificationExecutor<Inscripcion> {

    boolean existsByCurso_IdAndUsuario_Identificacion(Integer idCurso, String identificacion);

    @Query(value = "SELECT COUNT(*) > 0 FROM inscripciones WHERE IDENTIFICACION = :identificacion AND ID_CURSOS = :idCurso",
            nativeQuery = true)
    boolean existeInscripcion(@Param("identificacion") Integer identificacion,
                              @Param("idCurso") Integer idCurso);

    @Query(value = "SELECT * FROM inscripciones WHERE IDENTIFICACION = :identificacion", nativeQuery = true)
    List<Inscripcion> findByIdentificacion(@Param("identificacion") Integer identificacion);
}
