package com.skillup.skillup.repository;

import com.skillup.skillup.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    List<Administrador> findByIdRol(Integer rol);
}