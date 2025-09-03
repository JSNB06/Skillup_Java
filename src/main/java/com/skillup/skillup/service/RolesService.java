package com.skillup.skillup.service;

import com.skillup.skillup.model.Roles;
import com.skillup.skillup.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolRepository;


    public Roles verificarRoles(String usuario, String password) {
        return rolRepository.findByCorreoAndContrasena(usuario, password);
    }
}