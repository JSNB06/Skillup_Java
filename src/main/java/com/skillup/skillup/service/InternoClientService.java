package com.skillup.skillup.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class InternoClientService {

    private final RestTemplate restTemplate;

    public InternoClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Consumir lista simple de cursos
    public List<String> consumirMisCursos() {
        String url = "http://localhost:8080/api/miscursos";
        String[] cursosArray = restTemplate.getForObject(url, String[].class);
        return Arrays.asList(cursosArray);
    }

    // Consumir detalle de cursos
    public List<Map> consumirDetalle() {
        String url = "http://localhost:8080/api/miscursos/detalle";
        ResponseEntity<List> resp = restTemplate.getForEntity(url, List.class);
        return resp.getBody();
    }
}