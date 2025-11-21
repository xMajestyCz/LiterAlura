package com.henao.literalura.app;

import com.henao.literalura.model.*;
import com.henao.literalura.repository.AutorRepository;
import com.henao.literalura.repository.LibroRepository;
import com.henao.literalura.service.ConsumoAPI;
import com.henao.literalura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Scanner;

@Component
public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private static final String URL_BASE = "https://gutendex.com/books?search=";

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    // ===========================================================
    //                      MENÚ PRINCIPAL
    // ===========================================================
    public void mostrarMenu() {
        String opcion = "";

        while (!opcion.equals("0")) {

            System.out.println("""
                    1 - Buscar Libro
                    2 - Mostrar lista de libros
                    3 - Mostrar lista de libros por idioma
                    4 - Mostrar lista de autores
                    5 - Mostrar lista de autores vivos en determinado año
                    
                    0 - Salir
                    """);

            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> buscarLibroWeb();
                case "2" -> mostrarLibrosBuscados();
                case "3" -> mostrarLibrosPorIdioma();
                case "4" -> mostrarListaDeAutores();
                case "5" -> mostrarListaDeAutoresVivosEnDeterminadoAnio();
                case "0" -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    // ===========================================================
    //                    BÚSQUEDA DE LIBRO
    // ===========================================================
    protected DatosLibro getDatosLibro() {
        System.out.print("Escriba el nombre del libro que desea buscar: ");
        String nombreLibro = scanner.nextLine().trim();

        if (nombreLibro.isBlank()) {
            System.out.println("El nombre del libro no puede estar vacío.");
            return null;
        }

        try {
            String url = URL_BASE + URLEncoder.encode(nombreLibro, "UTF-8");

            String json = consumoAPI.obtenerDatos(url);

            ResultadoDeLibros resultado =
                    conversor.obtenerDatos(json, ResultadoDeLibros.class);

            if (resultado.libros() == null || resultado.libros().isEmpty()) {
                System.out.println("No se encontraron libros para la búsqueda.");
                return null;
            }

            return resultado.libros().get(0);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al codificar la URL", e);
        }
    }

    protected void buscarLibroWeb() {
        DatosLibro datos = getDatosLibro();
        if (datos == null) return;

        if (libroRepository.existsByTitulo(datos.titulo())) {
            System.out.println("El libro ya existe en la base de datos.");
            return;
        }

        Libro libro = new Libro(datos);

        Autor autorExistente =
                autorRepository.findByNombreAndFechaNacimientoAndFechaMuerte(
                        libro.getAutor().getNombre(),
                        libro.getAutor().getFechaNacimiento(),
                        libro.getAutor().getFechaMuerte()
                );

        if (autorExistente != null) {
            libro.setAutor(autorExistente);
        } else {
            autorRepository.save(libro.getAutor());
        }

        libroRepository.save(libro);
        System.out.println("Libro guardado: " + libro.getTitulo());
    }

    // ===========================================================
    //                      LISTADOS
    // ===========================================================
    protected void mostrarLibrosBuscados() {
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    protected void mostrarLibrosPorIdioma() {
        System.out.println("""
            Escriba el idioma por el que quiera buscar el libro:
            (solo se acepta 'Ingles' o 'Español')
        """);

        String entrada = scanner.nextLine().trim();
        Idioma idioma = Idioma.fromNormal(entrada);

        if (idioma == null) {
            System.out.println("Idioma mal escrito.");
            return;
        }

        List<Libro> libros = libroRepository.findByIdioma(idioma);

        System.out.println("Lista de libros en el idioma " + entrada);
        libros.forEach(System.out::println);
    }

    protected void mostrarListaDeAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    protected void mostrarListaDeAutoresVivosEnDeterminadoAnio() {
        System.out.print("Ingrese el año: ");
        String entrada = scanner.nextLine().trim();

        if (!entrada.matches("\\d+")) {
            System.out.println("Debe ingresar un año válido (solo números).");
            return;
        }

        int anio = Integer.parseInt(entrada);

        List<Autor> autores = autorRepository.vivo(anio);

        System.out.println("Autores vivos en el año " + anio);
        autores.forEach(System.out::println);
    }
}
