package com.henao.literalura.repository;

import com.henao.literalura.model.Idioma;
import com.henao.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Boolean existsByTitulo(String titulo);
    List<Libro> findByIdioma(Idioma idioma);
}
