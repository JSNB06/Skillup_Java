package com.skillup.skillup.service;

import com.skillup.skillup.model.Registrarse;
import com.skillup.skillup.repository.RegistrarseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RegistrarseService {

    @Autowired
    private RegistrarseRepository registrarseRepository;

    // Guardar usuario (crear o actualizar)
    public Registrarse guardarUsuario(Registrarse registrarse) {
        // Validar que no exista ya la identificación (para nuevos usuarios)
        if (registrarseRepository.existsByIdentificacion(registrarse.getIdentificacion())) {
            throw new RuntimeException("Ya existe un usuario con esta identificación.");
        }

        // Validar que no exista ya el correo
        if (registrarseRepository.existsByCorreo(registrarse.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con este correo.");
        }

        return registrarseRepository.save(registrarse);
    }

    // Actualizar usuario existente
    public Registrarse actualizarUsuario(Registrarse registrarse) {
        // Verificar que el usuario existe
        if (!registrarseRepository.existsByIdentificacion(registrarse.getIdentificacion())) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        // Verificar que el correo no esté en uso por otro usuario
        Optional<Registrarse> usuarioExistente = registrarseRepository.findByCorreo(registrarse.getCorreo());
        if (usuarioExistente.isPresent() &&
                !usuarioExistente.get().getIdentificacion().equals(registrarse.getIdentificacion())) {
            throw new RuntimeException("Ya existe otro usuario con este correo.");
        }

        return registrarseRepository.save(registrarse);
    }

    // Obtener todos los usuarios
    public List<Registrarse> obtenerTodosLosUsuarios() {
        return registrarseRepository.findAll();
    }

    // Obtener usuario por identificación
    public Optional<Registrarse> obtenerUsuarioPorId(String identificacion) {
        return registrarseRepository.findById(identificacion);
    }

    // Obtener usuario por correo
    public Optional<Registrarse> obtenerUsuarioPorCorreo(String correo) {
        return registrarseRepository.findByCorreo(correo);
    }

    // Obtener usuarios por rol
    public List<Registrarse> obtenerUsuariosPorRol(Integer idRol) {
        return registrarseRepository.findByIdRol(idRol);
    }

    // Buscar usuarios por nombre completo
    public List<Registrarse> buscarPorNombreCompleto(String nombreCompleto) {
        return registrarseRepository.findByNombreCompleto(nombreCompleto);
    }

    // Verificar credenciales para login
    public Optional<Registrarse> autenticarUsuario(String correo, String contraseña) {
        return registrarseRepository.findByCorreoAndContraseña(correo, contraseña);
    }

    // Eliminar usuario por identificación
    public void eliminarUsuario(String identificacion) {
        if (!registrarseRepository.existsByIdentificacion(identificacion)) {
            throw new RuntimeException("Usuario no encontrado.");
        }
        registrarseRepository.deleteById(identificacion);
    }

    // Verificar si existe usuario por identificación
    public boolean existeUsuario(String identificacion) {
        return registrarseRepository.existsByIdentificacion(identificacion);
    }

    // Verificar si existe usuario por correo
    public boolean existeUsuarioPorCorreo(String correo) {
        return registrarseRepository.existsByCorreo(correo);
    }

    // Cambiar contraseña
    public Registrarse cambiarContraseña(String identificacion, String nuevaContraseña) {
        Optional<Registrarse> usuarioOpt = registrarseRepository.findById(identificacion);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        Registrarse usuario = usuarioOpt.get();
        usuario.setContraseña(nuevaContraseña);
        return registrarseRepository.save(usuario);
    }
}