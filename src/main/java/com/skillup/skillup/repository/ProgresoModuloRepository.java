package com.skillup.skillup.repository;

import com.skillup.skillup.model.ProgresoModulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgresoModuloRepository extends JpaRepository<ProgresoModulo, Integer> {


    List<ProgresoModulo> findByIdUsuarioAndModulo_Curso_Id(Integer idUsuario, Integer idCurso);

    @Query("SELECT COUNT(pm) FROM ProgresoModulo pm WHERE pm.idUsuario = :idUsuario AND pm.modulo.curso.id = :idCurso AND pm.completado = true")
    Long countModulosCompletadosByCurso(@Param("idUsuario") Integer idUsuario, @Param("idCurso") Integer idCurso);

    @Query("SELECT COUNT(m) FROM Modulo m WHERE m.curso.id = :idCurso")
    Long countTotalModulosByCurso(@Param("idCurso") Integer idCurso);

    @Query("SELECT COUNT(pm) FROM ProgresoModulo pm WHERE pm.idUsuario = :idUsuario AND pm.completado = :completado")
    long countByIdUsuarioAndCompletado(@Param("idUsuario") Integer idUsuario, @Param("completado") boolean completado);
}