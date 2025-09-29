package com.skillup.skillup.service;

import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    public Optional<Usuarios> verificarRoles(String correo, String contrasena){
        return loginRepository.findByCorreoAndContrasena(correo, contrasena);
    }
}
