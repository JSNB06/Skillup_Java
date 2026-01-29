package com.skillup.skillup.service;

import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private EmailService emailService;


    public List<Usuarios> findAll() {
        return (List<Usuarios>)usuariosRepository.findAll();
    }

    public Optional<Usuarios> findById(String id) {
        return usuariosRepository.findById(id);
    }

    @Transactional
    public Usuarios save(Usuarios usuario) {

        Usuarios nuevo = usuariosRepository.save(usuario);


        emailService.sendWelcomeEmail(
                usuario.getCorreo(),
                usuario.getIdentificacion()
        );

        return nuevo;
    }

    @Transactional
    public void deleteByIdRol(String idRol) {
        usuariosRepository.deleteById(idRol);
    }

    public List<Usuarios> findByRol(Integer idRol) {
        return usuariosRepository.findByIdRol(idRol);
    }
}