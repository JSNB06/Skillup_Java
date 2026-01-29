package com.skillup.skillup.service;

import com.skillup.skillup.Dto.CursoReporteDto;
import com.skillup.skillup.repository.CursoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteJasperService {

    private final CursoRepository cursoRepository;

    public ReporteJasperService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public byte[] generarReporteCursosPdf(Map<String, Object> parametros) {
        try {
            //  Obtener datos de la BD
            List<CursoReporteDto> datosCursos = obtenerDatosCursos();

            //  Validar que haya datos
            if (datosCursos.isEmpty()) {
                throw new RuntimeException("No hay cursos registrados para generar el reporte");
            }

            //  Calcular totales (asegurarse de que sean Integer)
            Integer totalCursos = cursoRepository.contarTotalCursos();
            Integer totalInscripciones = cursoRepository.contarTotalInscripciones();

            //  Agregar parámetros
            parametros.put("TOTAL_CURSOS", totalCursos);
            parametros.put("TOTAL_INSCRIPCIONES", totalInscripciones);

            // Debug: Imprimir para verificar
            System.out.println("=== DEBUG REPORTE ===");
            System.out.println("Total Cursos: " + totalCursos + " (Tipo: " + totalCursos.getClass().getName() + ")");
            System.out.println("Total Inscripciones: " + totalInscripciones + " (Tipo: " + totalInscripciones.getClass().getName() + ")");
            System.out.println("Cantidad de cursos en lista: " + datosCursos.size());

            //  Cargar plantilla
            InputStream reporteStream =
                    getClass().getResourceAsStream("/reports/reporte_cursos.jrxml");

            if (reporteStream == null) {
                throw new RuntimeException("No se encontró el archivo reporte_cursos.jrxml");
            }

            //  Compilar
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

            //  DataSource
            JRBeanCollectionDataSource dataSource =
                    new JRBeanCollectionDataSource(datosCursos);

            //  Llenar
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            //  Exportar
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Error generando el reporte Jasper: " + e.getMessage(), e);
        }
    }

    private List<CursoReporteDto> obtenerDatosCursos() {
        List<Object[]> resultados = cursoRepository.obtenerReporteCursosNativo();

        return resultados.stream()
                .map(row -> {
                    String nombreCurso = (String) row[0];
                    Long totalInscripciones = ((Number) row[1]).longValue();

                    System.out.println("Curso: " + nombreCurso + " - Inscripciones: " + totalInscripciones);

                    return new CursoReporteDto(nombreCurso, totalInscripciones);
                })
                .collect(Collectors.toList());
    }
}