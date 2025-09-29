package com.skillup.skillup.controller;

import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping
    public String index(){
        return "login";
    }

    @PostMapping("/acceder")
    public String acceder(@RequestParam("usuario") String correo, @RequestParam("password") String password, HttpSession session, Model model){
        Optional<Usuarios> usuarioOpt = loginService.verificarRoles(correo, password);

        if (usuarioOpt.isPresent()){
            Usuarios usuario = usuarioOpt.get();

            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido1() + " " + usuario.getApellido2();

            session.setAttribute("roles_sistema", usuario.getIdentificacion());
            session.setAttribute("rol", usuario.getIdRol());
            session.setAttribute("nombre_usuario", nombreCompleto);

            switch (usuario.getIdRol()){
                case 1: return "redirect:/administrador/seleccionar";
                case 2: return "redirect:/estudiante";
                case 3: return "redirect:/evaluador/inicio";
                default: return "redirect:/login";
            }
        }else {
            model.addAttribute("mensaje", "Credenciales incorrectes");
            return "login";
        }

    }

    @GetMapping("/salir")
    public String salir(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
