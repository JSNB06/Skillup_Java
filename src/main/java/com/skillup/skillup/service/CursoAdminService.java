package com.skillup.skillup.service;


import com.skillup.skillup.model.CursoAdmin;
import com.skillup.skillup.repository.CursoAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoAdminService {

    @Autowired
    private CursoAdminRepository cursoAdminRepository;

    public List<CursoAdmin> filtrarCursos(String nombreCurso, String nombreUsuario, String identificacion) {

        // Convierte las cadenas vacías a null para que la consulta del repositorio funcione correctamente
        String nombreCursoParam = (nombreCurso != null && nombreCurso.isEmpty()) ? null : nombreCurso;
        String nombreUsuarioParam = (nombreUsuario != null && nombreUsuario.isEmpty()) ? null : nombreUsuario;
        String identificacionParam = (identificacion != null && identificacion.isEmpty()) ? null : identificacion;

        return cursoAdminRepository.filtrar(nombreCursoParam, nombreUsuarioParam, identificacionParam);
    }
}