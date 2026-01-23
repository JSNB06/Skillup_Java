package com.skillup.skillup.service;

import com.skillup.skillup.model.Contenido;
import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.repository.ContenidoRepository;
import com.skillup.skillup.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContenidoService {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    // Obtener contenidos de un módulo
    public List<Contenido> obtenerContenidosPorModulo(Integer idModulo) {
        return contenidoRepository.findByModulo_IdOrderByOrdenAsc(idModulo);
    }

    // Crear nuevo contenido
    @Transactional
    public Contenido crearContenido(Integer idModulo, String titulo, String descripcion, Integer orden) {
        Modulo modulo = moduloRepository.findById(idModulo)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));

        Contenido contenido = new Contenido();
        contenido.setModulo(modulo);
        contenido.setTitulo(titulo);
        contenido.setDescripcion(descripcion);
        contenido.setOrden(orden != null ? orden : 1);

        return contenidoRepository.save(contenido);
    }

    // Actualizar contenido
    @Transactional
    public Contenido actualizarContenido(Integer idContenido, String titulo, String descripcion, Integer orden) {
        Contenido contenido = contenidoRepository.findById(idContenido)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        contenido.setTitulo(titulo);
        contenido.setDescripcion(descripcion);
        if (orden != null) {
            contenido.setOrden(orden);
        }

        return contenidoRepository.save(contenido);
    }

    // Eliminar contenido
    @Transactional
    public void eliminarContenido(Integer idContenido) {
        contenidoRepository.deleteById(idContenido);
    }

    // Obtener un contenido por ID
    public Contenido obtenerContenidoPorId(Integer idContenido) {
        return contenidoRepository.findById(idContenido)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));
    }
}