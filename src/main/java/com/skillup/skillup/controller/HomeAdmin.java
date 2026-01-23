package com.skillup.skillup.controller;


import com.skillup.skillup.model.Rol;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.repository.RolRepository;
import com.skillup.skillup.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeAdmin {

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/administrador/seleccionar")
    public String seleccionar(){
        return "administrador/homeAdmin";
    }

    @GetMapping("/cursos/crear")
    public String crearCurso() {
        return "administrador/crearCurso";
    }



    @PostMapping("/administrador/filtrar")
    public String filtrar(@RequestParam("rol") String rol, Model model){

        if("correo".equals(rol)){
            return "redirect:/correo/enviar";
        }

        if("cursos".equals(rol)){
            return "redirect:/cursosadmin";
        }

        if("crearCurso".equals(rol)){
            return "redirect:/cursos/crear";
        }
        if("gestionarCursos".equals(rol)){
            return "redirect:/administrador/cursos/listar";
        }

        List<Usuarios> usuariosFiltrados;

        try{
            int idRol = Integer.parseInt(rol);
            usuariosFiltrados = usuariosRepository.findByIdRol(idRol);
        }catch (NumberFormatException e){
            usuariosFiltrados = Collections.emptyList();
            System.err.println("Error al obtener el rol "+rol);

        }

        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("usuariosFiltrados", usuariosFiltrados);
        model.addAttribute("roles", roles);

        return "administrador/administrador";
    }
}