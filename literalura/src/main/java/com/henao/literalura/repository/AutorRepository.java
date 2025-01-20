package com.henao.literalura.repository;

import com.henao.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Autor findByNombreAndFechaNacimientoAndFechaMuerte(String nombre, Integer fechaNacimiento, Integer fechaMuerte);

    @Query("""
        SELECT a FROM Autor a
        WHERE a.fechaNacimiento <= :anio
        AND (a.fechaMuerte IS NULL OR a.fechaMuerte >= :anio)
    """)
    List<Autor> vivo(int anio);



}
