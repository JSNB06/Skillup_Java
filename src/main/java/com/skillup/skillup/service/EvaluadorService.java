package com.skillup.skillup.service;


import com.skillup.skillup.model.Evaluador;
import com.skillup.skillup.repository.EvaluadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluadorService {

    private final EvaluadorRepository evaluadorRepository;

    public EvaluadorService(EvaluadorRepository evaluadorRepository) {
        this.evaluadorRepository = evaluadorRepository;
    }

    public List<Evaluador> getEvaluacionesPorEvaluador(String idEvaluador){
        return evaluadorRepository.findEvaluacionesDelEvaluador(idEvaluador);
    }

    public List<Evaluador> getTodasLasEvaluaciones(){
        return evaluadorRepository.findAll();
    }
}