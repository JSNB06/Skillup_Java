package com.skillup.skillup.controller;

import com.skillup.skillup.model.Rol;
import com.skillup.skillup.model.Usuarios;
import com.skillup.skillup.service.RolService;
import com.skillup.skillup.service.UsuariosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
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
    public String crear (Model model) {
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
    public String editar(@PathVariable String id, Model model) {
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

    @ModelAttribute
    public void agregarDatosSesion(Model model, HttpSession session, HttpServletResponse response) throws IOException {
        Object roles = session.getAttribute("roles_sistema");
        if (roles == null) {
            response.sendRedirect("/login");  // redirige si no tiene rol
            return;
        }
        model.addAttribute("nombreUsuario", session.getAttribute("nombre_usuario"));
    }


    @GetMapping("/exportar/{idRol}")
    public void exportarExcel(@PathVariable("idRol") Integer idRol, HttpServletResponse response) throws IOException {

        List<Usuarios> usuarios = usuariosService.findByRol(idRol);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("IDENTIFICACION");
        header.createCell(1).setCellValue("NOMBRE");
        header.createCell(2).setCellValue("PRIMER APELLIDO");
        header.createCell(3).setCellValue("SEGUNDO APELLIDO");
        header.createCell(4).setCellValue("CORREO");

        int rowNum = 1;
        for (Usuarios u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getIdentificacion());
            row.createCell(1).setCellValue(u.getNombre());
            row.createCell(2).setCellValue(u.getApellido1());
            row.createCell(3).setCellValue(u.getApellido2());
            row.createCell(4).setCellValue(u.getCorreo());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "Usuarios_Rol_" + idRol +  "_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}

