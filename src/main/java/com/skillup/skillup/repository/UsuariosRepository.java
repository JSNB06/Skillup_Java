package com.skillup.skillup.repository;

import com.skillup.skillup.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, String> {

 List<Usuarios> findByIdRol(Integer idRol);

    Optional<Usuarios> findByIdentificacion(String identificacion);

    List<Usuarios> idRol(Integer idRol);
}
