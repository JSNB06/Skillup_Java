package com.skillup.skillup.service;

import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.repository.CursoRepository;
import com.skillup.skillup.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModuloService {

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // Obtener todos los módulos
    public List<Modulo> obtenerTodosLosModulos() {
        return moduloRepository.findAll();
    }

    // Obtener módulo por ID
    public Modulo obtenerModuloPorId(Integer idModulo) {
        return moduloRepository.findById(idModulo)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + idModulo));
    }

    // Obtener módulos de un curso específico
    public List<Modulo> obtenerModulosPorCurso(Integer idCurso) {
        return moduloRepository.findByCurso_IdOrderByOrdenAsc(idCurso);
    }

    // Crear nuevo módulo
    @Transactional
    public Modulo crearModulo(Integer idCurso, String nombreModulo, String descripcion, Integer orden) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + idCurso));

        Modulo modulo = new Modulo();
        modulo.setCurso(curso);
        modulo.setNombre(nombreModulo);
        modulo.setDescripcion(descripcion);
        modulo.setOrden(orden != null ? orden : 1);

        return moduloRepository.save(modulo);
    }

    // Actualizar módulo existente
    @Transactional
    public Modulo actualizarModulo(Integer idModulo, String nombreModulo, String descripcion, Integer orden) {
        Modulo modulo = moduloRepository.findById(idModulo)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado con ID: " + idModulo));

        modulo.setNombre(nombreModulo);
        modulo.setDescripcion(descripcion);
        if (orden != null) {
            modulo.setOrden(orden);
        }

        return moduloRepository.save(modulo);
    }

    // Eliminar módulo
    @Transactional
    public void eliminarModulo(Integer idModulo) {
        if (!moduloRepository.existsById(idModulo)) {
            throw new RuntimeException("Módulo no encontrado con ID: " + idModulo);
        }
        moduloRepository.deleteById(idModulo);
    }

    // Contar módulos de un curso
    public long contarModulosPorCurso(Integer idCurso) {
        return moduloRepository.findByCurso_IdOrderByOrdenAsc(idCurso).size();
    }

    public Integer obtenerCursoDelModulo(Integer idModulo) {
        Modulo modulo = moduloRepository.findById(idModulo)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        return modulo.getCurso().getId();
    }
}