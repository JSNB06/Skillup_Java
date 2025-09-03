package com.skillup.skillup.controller;

 import com.skillup.skillup.model.Administrador;
 import com.skillup.skillup.service.AdministradorService;
 import jakarta.servlet.http.HttpSession;
 import org.apache.poi.ss.usermodel.Row;
 import org.apache.poi.ss.usermodel.Sheet;
 import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 import org.springframework.http.HttpHeaders;
 import org.springframework.http.MediaType;
 import org.springframework.http.ResponseEntity;
 import org.springframework.stereotype.Controller;
 import org.springframework.ui.Model;
 import org.springframework.web.bind.annotation.*;

 import java.io.ByteArrayOutputStream;
 import java.util.List;


@Controller
@RequestMapping("/administrador")
public class AdministradorController {
    private final AdministradorService service;

    public AdministradorController(AdministradorService service) {
        this.service = service;
    }

    //INDEX (LA LISTA PARA LOS ADMINISTRADORES)
    @GetMapping
    public String index(HttpSession session, Model model){
        // ACA VALIDAMOS LA SESION DEL ADMIN
        if (session.getAttribute("roles_sistema")==null){
            return "redirect:/login";
        }
        if((int)session.getAttribute("rol") !=1){
            return "redirect:/login";
        }

        List<Administrador> lista = service.listarTodos();
        model.addAttribute("administrador", lista);
        return "/administrador/administrador";
    }

    // CODIGO PARA EL FORMULARIO DE CREAR
    @GetMapping("/crear")
    public String crear(){
        return "crear";
    }

    // CODIGO PARA GUARDAR UN NUEVO USUARIO
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Administrador admin){
        service.guardar(admin);
        return "redirect:/administrador";
    }

    // CODIGO PARA EL FORMULARIO DE EDITAR
    @GetMapping("/actualizar/{id}")
    public String editar(@PathVariable Long id, Model model){
        Administrador admin = service.buscarPorId(id);
        model.addAttribute("role", admin);
        return "editar";
    }

    // CODIGO PARA ACTUALIZAR

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable String id, @ModelAttribute Administrador admin){
        admin.setIdentificacion(id);
        service.guardar(admin);
        return "redirect:/administrador";
    }


    //CODIGO PARA ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id){
        service.eliminar(id);
        return "redirect:/administrador";
    }

    //EXPORTAR A EXCEL
    @GetMapping("/exportarExcel/{rol}")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Integer rol) throws  Exception{
        List<Administrador> lista = service.listarPorRol(rol);

        try (XSSFWorkbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Administradores");

            //ENCABEZADO DE EXCEL
             Row header = sheet.createRow(0);
             header.createCell(0).setCellValue("IDENTIFICACION");
            header.createCell(1).setCellValue("NOMBRE");
            header.createCell(2).setCellValue("PRIMER APELLIDO");
            header.createCell(3).setCellValue("SEGUNDO APELLIDO");
            header.createCell(4).setCellValue("CORREO");

            //DATOS PARA EL EXCEL

            int fila = 1;
            for (Administrador a : lista){
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(a.getIdentificacion());
                row.createCell(1).setCellValue(a.getNombre());
                row.createCell(2).setCellValue(a.getApellido1());
                row.createCell(3).setCellValue(a.getApellido2());
                row.createCell(4).setCellValue(a.getCorreo());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            String filename = "Usuario_rol" + System.currentTimeMillis() + ".xlsx";

                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(out.toByteArray());
        }
    }


}