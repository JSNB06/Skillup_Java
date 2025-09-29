package com.skillup.skillup.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "roles_sistema")
public class Registrarse {

    @Id
    @Column(name = "IDENTIFICACION")
    @NotNull(message = "La identificación es obligatoria.")
    @Pattern(regexp = "\\d{6,15}", message = "Solo se permiten números de 6 a 15 dígitos.")
    private String identificacion;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    @NotBlank(message = "El nombre es obligatorio.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$", message = "El nombre solo puede contener letras y espacios.")
    private String nombre;

    @Column(name = "APELLIDO1", nullable = false, length = 50)
    @NotBlank(message = "El primer apellido es obligatorio.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$", message = "Solo letras y espacios.")
    private String apellido1;

    @Column(name = "APELLIDO2", length = 50)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{0,50}$", message = "Solo letras y espacios.")
    private String apellido2;

    @Column(name = "CORREO", nullable = false, unique = true)
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Debe ser un correo válido.")
    private String correo;

    @Column(name = "CONTRASEÑA", nullable = false)
    @NotBlank(message = "Debe ingresar una contraseña.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String contraseña;

    @Column(name = "idRol", nullable = false)
    @NotNull(message = "El rol es obligatorio.")
    private Integer idRol;

    // Constructor vacío
    public Registrarse() {}

    // Constructor completo
    public Registrarse(String identificacion, String nombre, String apellido1, String apellido2,
                   String correo, String contraseña, Integer idRol) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.contraseña = contraseña;
        this.idRol = idRol;
    }

    // Getters y Setters
    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }
}