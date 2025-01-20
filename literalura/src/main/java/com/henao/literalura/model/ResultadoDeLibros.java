package com.henao.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultadoDeLibros(
        @JsonAlias("results") List<DatosLibro> libros
) {
}
