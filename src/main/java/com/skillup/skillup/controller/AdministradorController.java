package com.skillup.skillup.controller;

import com.skillup.skillup.model.Rol;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.service.RolService;
import com.skillup.skillup.service.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private UsuariosService usuariosService;

    @Autowired
    private RolService rolService;

    @GetMapping
    public String lstarAdministrador(Model model) {

        List<Usuarios> usuarios = (List<Usuarios>) usuariosService.findAll();
        model.addAttribute("usuariosFiltrados", usuarios);
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        model.addAttribute("roles", roles);
        return "administrador/administrador";
    }

    @PostMapping("/fitrar")
    public String filtrarPorRol(@RequestParam("idRol") Integer idRol, Model model) {
        List<Usuarios> usuariosFiltrados = usuariosService.findByRol(idRol);
        model.addAttribute("usuariosFiltrados", usuariosFiltrados);
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        model.addAttribute("roles", roles);
        return "administrador/administrador";
    }


    @GetMapping("/crear")
    public String crear (Model model){
        model.addAttribute("usuarios", new Usuarios());
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        model.addAttribute("roles", roles);
        return "administrador/crear";
    }

    @PostMapping("/guardar")
    public String guardar (@ModelAttribute Usuarios usuarios, RedirectAttributes redirectAttributes){
        usuariosService.save(usuarios);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado exitosamente");
        return "redirect:/administrador";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model){
        Optional<Usuarios> usuarios = usuariosService.findById(id);
        if(usuarios.isPresent()){
            model.addAttribute("usuarios", usuarios.get());
            List<Rol> roles = rolService.obtenerTodosLosRoles();
            model.addAttribute("roles", roles);
            return "administrador/editar";
        }
        return "redirect:/administrador";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable String id,
                             @ModelAttribute Usuarios usuarioForm,
                             RedirectAttributes redirectAttributes) {

        Usuarios usuarioBD = usuariosService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioBD.setNombre(usuarioForm.getNombre());
        usuarioBD.setApellido1(usuarioForm.getApellido1());
        usuarioBD.setApellido2(usuarioForm.getApellido2());
        usuarioBD.setContrasena(usuarioForm.getContrasena());
        usuarioBD.setCorreo(usuarioForm.getCorreo());

        usuariosService.save(usuarioBD);

        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");
        return "redirect:/administrador";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirectAttributes){
        usuariosService.deleteByIdRol(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado exitosamente");
        return "redirect:/administrador";
    }

}
