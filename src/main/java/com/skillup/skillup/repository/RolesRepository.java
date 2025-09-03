package com.skillup.skillup.repository;

import com.skillup.skillup.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    // Equivalente a verificarRoles($usuario,$password)

    Roles findByCorreoAndContrasena(String correo, String contrasena);
}