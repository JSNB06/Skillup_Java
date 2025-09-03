package com.skillup.skillup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa la tabla 'roles_sistema' en la base de datoss.
 * Esta clase actúa como el "modelo" o "dominio" de los datos.
 */
@Entity
@Table(name = "roles_sistema")
public class Administrador {

    /**
     * @Id: Marca el campo como la clave primaria de la tabla.
     * @Column: Mapea el campo de la clase a la columna de la base de datos.
     * En este caso, el nombre del campo en Java es el mismo que en la columna,
     * pero es una buena práctica usar la anotación.
     */
    @Id
    @Column(name = "IDENTIFICACION", nullable = false)
    private String identificacion;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "APELLIDO1", nullable = false)
    private String apellido1;

    @Column(name = "APELLIDO2", nullable = false)
    private String apellido2;

    @Column(name = "CONTRASEÑA", nullable = false)
    private String contrasena;

    @Column(name = "idRol", nullable = false)
    private Integer idRol;

    @Column(name = "CORREO", nullable = false)
    private String correo;

    // Constructor vacío (necesario para JPA)
    public Administrador() {
    }

    // Constructor con todos los campos
    public Administrador(String identificacion, String nombre, String apellido1, String apellido2, String contrasena, Integer idRol, String correo) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.correo = correo;
    }

    // Getters y Setters para todos los campos
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

