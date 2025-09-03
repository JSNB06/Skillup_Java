package com.skillup.skillup.controller;

import org.springframework.ui.Model;
import com.skillup.skillup.model.Roles;
import com.skillup.skillup.service.RolesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private RolesService rolService;

    // VISTA DE LOGIN

    @GetMapping
    public String index() {
        return "login";
    }

    //PROCESAR CREDENCIALES

    @PostMapping("/acceder")
    public String acceder(
            @RequestParam("usuario") String usuario,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Roles datosRol = rolService.verificarRoles(usuario, password);

        if (datosRol == null) {
            model.addAttribute("mensaje", "Credenciales Incorrectas");
            return "login";
        }

        //SI HAY UN ROL VALIDO

        session.setAttribute("roles_sistema", datosRol.getIdentificacion());
        session.setAttribute("rol", datosRol.getIdRol());
        session.setAttribute("nombre_usuario", datosRol.getNombre() + " " + datosRol.getApellido1() + " " + datosRol.getApellido2());
        switch (datosRol.getIdRol()) {
            case 1: return "redirect:/seleccionar";
            case 2: return "redirect:/estudiante";
            case 3: return "redirect:/evaluador/inicio";
            default: return "redirect:/login";
        }
}
}

