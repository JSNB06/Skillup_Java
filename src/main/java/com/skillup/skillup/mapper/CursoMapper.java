package com.skillup.skillup.mapper;

import com.skillup.skillup.Dto.ContenidoDTO;
import com.skillup.skillup.Dto.CursoDTO;
import com.skillup.skillup.Dto.ModuloDTO;
import com.skillup.skillup.model.Contenido;
import com.skillup.skillup.model.Curso;
import com.skillup.skillup.model.Modulo;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CursoMapper {

    public Curso toEntity(CursoDTO dto) {
        if (dto == null) {
            return null;
        }

        Curso curso = new Curso();
        curso.setNombre(dto.getNombre());

        curso.setImagenUrl(dto.getImagenUrl());

        if (dto.getModulos() != null && !dto.getModulos().isEmpty()) {
            Set<Modulo> modulos = dto.getModulos().stream()
                    .map(moduloDTO -> toModuloEntity(moduloDTO, curso))
                    .collect(Collectors.toSet());
            curso.setModulos(modulos);
        }

        return curso;
    }

    private Modulo toModuloEntity(ModuloDTO dto, Curso curso) {
        if (dto == null) {
            return null;
        }

        Modulo modulo = new Modulo();
        modulo.setNombre(dto.getNombre());
        modulo.setDescripcion(dto.getDescripcion());
        modulo.setOrden(dto.getOrden());
        modulo.setCurso(curso);

        if (dto.getContenidos() != null && !dto.getContenidos().isEmpty()) {

            List<Contenido> contenidos = dto.getContenidos().stream()
                    .map(contenidoDTO -> toContenidoEntity(contenidoDTO, modulo))
                    .collect(Collectors.toList()); // ← Cambio aquí: toList() en vez de toSet()
            modulo.setContenidos(contenidos);
        }

        return modulo;
    }

    private Contenido toContenidoEntity(ContenidoDTO dto, Modulo modulo) {
        if (dto == null) {
            return null;
        }

        Contenido contenido = new Contenido();
        contenido.setTitulo(dto.getTitulo());
        contenido.setDescripcion(dto.getDescripcion());
        contenido.setOrden(dto.getOrden());
        contenido.setModulo(modulo);

        return contenido;
    }

    public CursoDTO toDTO(Curso entity) {
        if (entity == null) {
            return null;
        }

        CursoDTO dto = new CursoDTO();
        dto.setNombre(entity.getNombre());

        dto.setImagenUrl(entity.getImagenUrl());


        if (entity.getModulos() != null && !entity.getModulos().isEmpty()) {
            Set<ModuloDTO> modulosDTO = entity.getModulos().stream()
                    .map(this::toModuloDTO)
                    .collect(Collectors.toSet());
            dto.setModulos(modulosDTO);
        }

        return dto;
    }

    private ModuloDTO toModuloDTO(Modulo entity) {
        if (entity == null) {
            return null;
        }

        ModuloDTO dto = new ModuloDTO();
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setOrden(entity.getOrden());

        if (entity.getContenidos() != null && !entity.getContenidos().isEmpty()) {

            Set<ContenidoDTO> contenidosDTO = entity.getContenidos().stream()
                    .map(this::toContenidoDTO)
                    .collect(Collectors.toSet());
            dto.setContenidos(contenidosDTO);
        }

        return dto;
    }

    private ContenidoDTO toContenidoDTO(Contenido entity) {
        if (entity == null) {
            return null;
        }

        ContenidoDTO dto = new ContenidoDTO();
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setOrden(entity.getOrden());

        return dto;
    }
}