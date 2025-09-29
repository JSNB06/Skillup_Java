package com.skillup.skillup.repository;

import com.skillup.skillup.model.Registrarse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrarseRepository extends JpaRepository<Registrarse, String> {

    Optional<Registrarse> findByCorreo(String correo);

    boolean existsByIdentificacion(String identificacion);

    boolean existsByCorreo(String correo);

    @Query("SELECT u FROM Registrarse u WHERE u.idRol = :idRol")
    java.util.List<Registrarse> findByIdRol(@Param("idRol") Integer idRol);

    // Buscar por nombre completo (nombre + apellidos)
    @Query("SELECT u FROM Registrarse u WHERE " +
            "CONCAT(u.nombre, ' ', u.apellido1, COALESCE(CONCAT(' ', u.apellido2), '')) " +
            "LIKE %:nombreCompleto%")
    java.util.List<Registrarse> findByNombreCompleto(@Param("nombreCompleto") String nombreCompleto);

    // Verificar credenciales para login
    @Query("SELECT u FROM Registrarse u WHERE u.correo = :correo AND u.contraseña = :contraseña")
    Optional<Registrarse> findByCorreoAndContraseña(@Param("correo") String correo,
                                                @Param("contraseña") String contraseña);

}