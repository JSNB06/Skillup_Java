package com.skillup.skillup.controller;


import com.skillup.skillup.service.ReporteJasperService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteJasperController {

    private final ReporteJasperService reporteJasperService;

    public ReporteJasperController(ReporteJasperService reporteJasperService) {
        this.reporteJasperService = reporteJasperService;
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cursos/pdf")
    public ResponseEntity<byte[]> descargarReporteCursos() {

        //  Preparar parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO", "Informe de Inscripciones por Curso");

        //  Formatear fecha actual
        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        parametros.put("FECHA_REPORTE", fechaActual);

        //  Generar PDF (el servicio obtiene los datos internamente)
        byte[] pdfBytes = reporteJasperService.generarReporteCursosPdf(parametros);

        //  Configurar headers para descarga directa
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Nombre del archivo con timestamp
        String nombreArchivo = "reporte_cursos_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".pdf";

        headers.setContentDispositionFormData("attachment", nombreArchivo);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        // Retornar el PDF
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}