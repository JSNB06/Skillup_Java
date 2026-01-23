package com.skillup.skillup.service;

import com.skillup.skillup.Dto.CursoDTO;
import com.skillup.skillup.mapper.CursoMapper;
import com.skillup.skillup.model.Contenido;
import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CursoMapper cursoMapper;

    @Transactional
    public Curso guardarCursoCompleto(CursoDTO cursoDTO) {
        if (cursoRepository.existsByNombre(cursoDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un curso con el nombre: " + cursoDTO.getNombre());
        }

        Curso curso = cursoMapper.toEntity(cursoDTO);
        return cursoRepository.save(curso);
    }

    public boolean existeCursoPorNombre(String nombre) {
        return cursoRepository.existsByNombre(nombre);
    }

    //Esto devuelve DTO - Es decir para la APIs REST
    public CursoDTO obtenerCursoPorId(Integer id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + id));
        return cursoMapper.toDTO(curso);
    }

    // Esto devuelve la entidad Curso con modulos (para las vistas)
    @Transactional(readOnly = true)
    public Curso obtenerCursoConModulos(Integer id) {
        return cursoRepository.findByIdWithModulosAndContenidos(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + id));
    }


    public Curso obtenerCursoEntity(Integer id) {
        return obtenerCursoConModulos(id);
    }

    //Esto obtiene todos los cursos
    @Transactional(readOnly = true)
    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }
}