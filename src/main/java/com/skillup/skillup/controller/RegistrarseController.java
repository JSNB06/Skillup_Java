package com.skillup.skillup.controller;

import com.skillup.skillup.model.Registrarse;
import com.skillup.skillup.service.RegistrarseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/registro")
public class RegistrarseController {

    @Autowired
    private RegistrarseService  registrarseService;


    private static final List<String> DOMINIOS_PERMITIDOS = Arrays.asList(
            "gmail.com", "hotmail.com", "outlook.com", "yahoo.com", "icloud.com", "skillup.com"
    );


    private static final Pattern PATTERN_IDENTIFICACION = Pattern.compile("^\\d{6,15}$");
    private static final Pattern PATTERN_NOMBRES = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$");
    private static final Pattern PATTERN_CONTRASEÑA = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");


    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("registrarse", new Registrarse());
        return "registro";
    }


    @PostMapping("/guardar")
    public String guardarUsuario(@RequestParam("documento") String identificacion,
                                 @RequestParam("nombre") String nombre,
                                 @RequestParam("apellido1") String apellido1,
                                 @RequestParam(value = "apellido2", required = false) String apellido2,
                                 @RequestParam("email") String correo,
                                 @RequestParam("password") String contraseña,
                                 @RequestParam("confirmPassword") String confirmarContraseña,
                                 RedirectAttributes redirectAttributes) {

        List<String> errores = new ArrayList<>();


        identificacion = identificacion != null ? identificacion.trim() : "";
        nombre = nombre != null ? nombre.trim() : "";
        apellido1 = apellido1 != null ? apellido1.trim() : "";
        apellido2 = apellido2 != null ? apellido2.trim() : "";
        correo = correo != null ? correo.trim() : "";


        if (identificacion.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() ||
                correo.isEmpty() || contraseña == null || contraseña.isEmpty() ||
                confirmarContraseña == null || confirmarContraseña.isEmpty()) {
            errores.add("Todos los campos obligatorios deben ser completados.");
        }

        if (!PATTERN_IDENTIFICACION.matcher(identificacion).matches()) {
            errores.add("La identificación debe contener solo números (6 a 15 dígitos).");
        }


        if (!PATTERN_NOMBRES.matcher(nombre).matches()) {
            errores.add("El nombre solo debe contener letras y espacios.");
        }


        if (!PATTERN_NOMBRES.matcher(apellido1).matches()) {
            errores.add("El primer apellido solo debe contener letras y espacios.");
        }


        if (!apellido2.isEmpty() && !PATTERN_NOMBRES.matcher(apellido2).matches()) {
            errores.add("El segundo apellido solo debe contener letras y espacios.");
        }


        if (!isValidEmail(correo)) {
            errores.add("Correo no válido.");
        } else {
            String dominio = extraerDominio(correo);
            if (!DOMINIOS_PERMITIDOS.contains(dominio)) {
                errores.add("Dominio de correo no permitido.");
            }
        }


        if (!PATTERN_CONTRASEÑA.matcher(contraseña).matches()) {
            errores.add("La contraseña debe tener al menos una mayúscula, una minúscula, un número y mínimo 8 caracteres.");
        }


        if (!contraseña.equals(confirmarContraseña)) {
            errores.add("Las contraseñas no coinciden.");
        }


        if (!errores.isEmpty()) {
            redirectAttributes.addFlashAttribute("errores", errores);
            return "redirect:/registro";
        }


        if (registrarseService.existeUsuario(identificacion)) {
            errores.add("Ya existe un usuario con esta identificación.");
            redirectAttributes.addFlashAttribute("errores", errores);
            return "redirect:/registro";
        }

        if (registrarseService.existeUsuarioPorCorreo(correo)) {
            errores.add("Ya existe un usuario con este correo.");
            redirectAttributes.addFlashAttribute("errores", errores);
            return "redirect:/registro";
        }

        try {

            Registrarse usuario = new Registrarse();
            usuario.setIdentificacion(identificacion);
            usuario.setNombre(nombre);
            usuario.setApellido1(apellido1);
            usuario.setApellido2(apellido2.isEmpty() ? null : apellido2);
            usuario.setCorreo(correo);
            usuario.setContraseña(contraseña); // En producción debería estar encriptada
            usuario.setIdRol(2); // Rol por defecto


            registrarseService.guardarUsuario(usuario);


            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
            return "redirect:/login";

        } catch (Exception e) {
            errores.add("Error al crear el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errores", errores);
            return "redirect:/registro";
        }
    }


    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }


        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(email).matches();
    }

    private String extraerDominio(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }
        String[] partes = email.split("@");
        return partes.length > 1 ? partes[1].toLowerCase() : "";
    }
}