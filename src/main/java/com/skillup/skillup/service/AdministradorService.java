package com.skillup.skillup.service;

import com.skillup.skillup.model.Administrador;
import com.skillup.skillup.repository.AdministradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorService{
    private final AdministradorRepository repository;

    public AdministradorService(AdministradorRepository repository){
        this.repository = repository;
    }
    // Listar todos
    public List<Administrador> listarTodos() {
        return repository.findAll();
    }

    // Guardar o actualizar
    public Administrador guardar(Administrador admin) {
        return repository.save(admin);
    }

    // Buscar por ID
    public Administrador buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    // Eliminar
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // Buscar por rol
    public List<Administrador> listarPorRol(Integer rol) {
        return repository.findByIdRol(rol);
    }
}

