package com.skillup.skillup.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public class CursoDTO {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    private String imagenUrl;

    @Valid
    private Set<ModuloDTO> modulos;

    public CursoDTO() {}

    public String getNombre() {
        return nombre;
    }


    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<ModuloDTO> getModulos() {
        return modulos;
    }

    public void setModulos(Set<ModuloDTO> modulos) {
        this.modulos = modulos;
    }
}