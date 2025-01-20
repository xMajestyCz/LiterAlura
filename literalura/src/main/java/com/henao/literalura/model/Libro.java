package com.henao.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private Integer numeroDeDescargas;
    @ManyToOne(fetch = FetchType.EAGER)
    private Autor autor;

    public Libro(){

    }

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        try {
            Idioma idiomaEncontrado = Idioma.fromString(datosLibro.idioma().get(0));
            if (idiomaEncontrado == Idioma.ESPAÑOL || idiomaEncontrado == Idioma.INGLES) {
                this.idioma = idiomaEncontrado;
            } else {
                this.idioma = null;
            }
        } catch (IllegalArgumentException e) {
            this.idioma = null;
        }
        this.numeroDeDescargas = datosLibro.numeroDeDescargas();
        this.autor = new Autor(datosLibro.autor().get(0));
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Integer getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Integer numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    @Override
    public String toString() {
        return "titulo='" + titulo + '\'' +
                ", idioma=" + idioma +
                ", numeroDeDescargas=" + numeroDeDescargas +
                ", autor=" + autor;
    }
}
