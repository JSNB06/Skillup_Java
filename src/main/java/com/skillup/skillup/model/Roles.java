package com.skillup.skillup.model;

import jakarta.persistence.*;


@Entity
@Table(name = "roles_sistema")
public class Roles {
    @Id
    @Column(name = "IDENTIFICACION")
    private Long identificacion;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "APELLIDO1")
    private String apellido1;

    @Column(name = "APELLIDO2")
    private String apellido2;

    @Column(name = "CONTRASEÑA") // Ojo: el nombre tiene tilde en BD
    private String contrasena;

    @Column(name = "idRol")
    private Integer idRol;

    @Column(name = "CORREO")
    private String correo;

    // ===== Getters y Setters =====
    public Long getIdentificacion() { return identificacion; }
    public void setIdentificacion(Long identificacion) { this.identificacion = identificacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido1() { return apellido1; }
    public void setApellido1(String apellido1) { this.apellido1 = apellido1; }

    public String getApellido2() { return apellido2; }
    public void setApellido2(String apellido2) { this.apellido2 = apellido2; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

}