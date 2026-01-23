package com.skillup.skillup.service;

import com.skillup.skillup.model.Modulo;
import com.skillup.skillup.model.ProgresoModulo;
import com.skillup.skillup.repository.ModuloRepository;
import com.skillup.skillup.repository.ProgresoModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgresoModuloService {

    @Autowired
    private ProgresoModuloRepository progresoRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    // Marcar módulo como completado
    @Transactional
    public ProgresoModulo marcarModuloCompletado(Integer idUsuario, Integer idModulo) {
        Modulo modulo = moduloRepository.findById(idModulo)
                .orElseThrow(() -> new IllegalArgumentException("Módulo no encontrado"));

        List<ProgresoModulo> progresos = progresoRepository.findByIdUsuarioAndModulo_Curso_Id(
                idUsuario,
                modulo.getCurso().getId()
        );

        ProgresoModulo progreso = progresos.stream()
                .filter(p -> p.getModulo().getId().equals(idModulo))
                .findFirst()
                .orElse(new ProgresoModulo());

        progreso.setIdUsuario(idUsuario);
        progreso.setModulo(modulo);
        progreso.setCompletado(true);
        progreso.setFechaCompletado(LocalDateTime.now());

        return progresoRepository.save(progreso);
    }

    // VERIFICA SI SE PUEDE HACER LA EVALUACIOMN
    public boolean puedeHacerEvaluacion(Integer idUsuario, Integer idCurso) {
        Long modulosCompletados = progresoRepository.countModulosCompletadosByCurso(idUsuario, idCurso);
        Long totalModulos = progresoRepository.countTotalModulosByCurso(idCurso);

        return modulosCompletados.equals(totalModulos) && totalModulos > 0;
    }

    // OBTENER PROGRESO DE UN USUARIO A UN CURSO
    public List<ProgresoModulo> obtenerProgresoPorUsuarioYCurso(Integer idUsuario, Integer idCurso) {
        return progresoRepository.findByIdUsuarioAndModulo_Curso_Id(idUsuario, idCurso);
    }

   // CALCULAR PORCENTAJE DE AVANCER
    public double calcularPorcentajeAvance(Integer idUsuario, Integer idCurso) {
        Long modulosCompletados = progresoRepository.countModulosCompletadosByCurso(idUsuario, idCurso);
        Long totalModulos = progresoRepository.countTotalModulosByCurso(idCurso);

        if (totalModulos == 0) return 0;

        return (modulosCompletados * 100.0) / totalModulos;
    }
}