package com.skillup.skillup.repository;

import com.skillup.skillup.model.Registrarse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrarseRepository extends JpaRepository<Registrarse, Long> {

    boolean existsByIdentificacion(String identificacion);

    boolean existsByCorreo(String correo);

    Registrarse findByIdentificacion(String identificacion);

    Registrarse findByCorreo(String correo);
}