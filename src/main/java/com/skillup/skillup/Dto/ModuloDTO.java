package com.skillup.skillup.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.Set;

public class ModuloDTO {

    @NotBlank(message = "El nombre del módulo es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre del módulo debe tener entre 3 y 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    private String descripcion;

    @NotNull(message = "El orden del módulo es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor a 0")
    private Integer orden;

    @Valid
    private Set<ContenidoDTO> contenidos;

    public ModuloDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Set<ContenidoDTO> getContenidos() {
        return contenidos;
    }

    public void setContenidos(Set<ContenidoDTO> contenidos) {
        this.contenidos = contenidos;
    }
}