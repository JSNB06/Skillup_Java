package com.skillup.skillup.repository;

import com.skillup.skillup.model.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<Contenido, Integer> {

    List<Contenido> findByModulo_IdOrderByOrdenAsc(Integer idModulo);

    void deleteByModulo_Id(Integer idModulo);
}