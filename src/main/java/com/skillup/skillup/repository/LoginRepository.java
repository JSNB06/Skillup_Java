package com.skillup.skillup.repository;

import com.skillup.skillup.model.Usuarios;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByCorreoAndContrasena(String correo, String contrasena);


}
