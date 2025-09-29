package com.skillup.skillup.repository;

import com.skillup.skillup.model.CursoAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CursoAdminRepository extends JpaRepository<CursoAdmin, Integer> {

    List<CursoAdmin> findByIdCursoIn(List<Long> ids);


    List<CursoAdmin> findByUsuariosIdentificacion( String identificacion );

    @Query("SELECT c FROM CursoAdmin c JOIN FETCH c.usuarios u WHERE " +
            "(:nombreCurso IS NULL OR c.nombreCurso LIKE %:nombreCurso%) AND " +
            "(:nombreUsuario IS NULL OR u.nombre LIKE %:nombreUsuario%) AND " +
            "(:identificacion IS NULL OR u.identificacion LIKE %:identificacion%)")
    List<CursoAdmin> filtrar(
            @Param("nombreCurso") String nombreCurso,
            @Param("nombreUsuario") String nombreUsuario,
            @Param("identificacion") String identificacion
    );
}