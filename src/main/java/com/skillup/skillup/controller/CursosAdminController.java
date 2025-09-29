package com.skillup.skillup.controller;


import com.skillup.skillup.model.CursoAdmin;
import com.skillup.skillup.repository.CursoAdminRepository;
import com.skillup.skillup.service.CursoAdminService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class CursosAdminController {

    @Autowired
    private CursoAdminRepository cursoAdminRepository;

    @Autowired
    private CursoAdminService cursoAdminService;


    @GetMapping("/cursosadmin")
    public String index(Model model) {
        List<CursoAdmin> cursos = cursoAdminRepository.findAll();
        model.addAttribute("cursos", cursos);
        return "administrador/cursosadmin";
    }

    @PostMapping("/cursosadmin/filtrar")
    public String filtrarCursos(
            @RequestParam(name = "nombre", required = false) String nombreCurso,
            @RequestParam(name = "estudiante", required = false) String nombreUsuario,
            @RequestParam(name = "identificacion", required = false) String identificacion,
            Model model
    ) {
        List<CursoAdmin> cursosFiltrados = cursoAdminService.filtrarCursos(
                nombreCurso, nombreUsuario, identificacion
        );

        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("oldNombre", nombreCurso);
        model.addAttribute("oldEstudiante", nombreUsuario);
        model.addAttribute("oldIdentificacion", identificacion);

        return "administrador/cursosadmin";
    }

    @GetMapping("/cursosadmin/exportar")
    public void exportarExcel(@RequestParam(name = "nombre", required = false) String nombreCurso,
                              @RequestParam(name = "estudiante", required = false) String nombreUsuario,
                              @RequestParam(name = "identificacion", required = false) String identificacion,
                              HttpServletResponse response) throws IOException {

        List<CursoAdmin> cursos = cursoAdminService.filtrarCursos(nombreCurso, nombreUsuario, identificacion);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("cursos");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Nombre del Curso");
        header.createCell(1).setCellValue("Nombre");
        header.createCell(2).setCellValue("Apellido");
        header.createCell(3).setCellValue("Identificacion");

        int rowNum = 1;
        for (CursoAdmin curso : cursos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(curso.getNombreCurso() != null ? curso.getNombreCurso() : "");

            if(curso.getUsuarios() != null){
                row.createCell(1).setCellValue(curso.getUsuarios().getNombre() != null ? curso.getUsuarios().getNombre() : "");
                row.createCell(2).setCellValue(curso.getUsuarios().getApellido1() != null ? curso.getUsuarios().getApellido1() : "");
                row.createCell(3).setCellValue(curso.getUsuarios().getIdentificacion() != null ? curso.getUsuarios().getIdentificacion() : "");
            }else {
                row.createCell(1).setCellValue("");
                row.createCell(2).setCellValue("");
                row.createCell(3).setCellValue("");
            }
        }

        response.setContentType("application/vnd.openxmlformtats-officedocument.spreadsheetml.sheet");
        String filename = "Cursos_Filtrados_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        workbook.write(response.getOutputStream());
        workbook.close();
    }


}
