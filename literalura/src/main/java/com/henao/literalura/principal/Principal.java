package com.henao.literalura.principal;

import com.henao.literalura.model.*;
import com.henao.literalura.repository.AutorRepository;
import com.henao.literalura.repository.LibroRepository;
import com.henao.literalura.service.ConsumoAPI;
import com.henao.literalura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private final String URL_BASE = "https://gutendex.com/books?search=";
    private List<Libro> libros;
    private List<Autor> autores;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository){
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }
    public void mostrarMenu(){
        var opcion = 1;
        while (opcion != 0) {
            var menu = """
                        1 - Buscar Libro
                        2 - Mostrar lista de libros
                        3 - Mostrar lista de libros por idioma
                        4 - Mostrar lista de autores
                        5 - Mostrar lista de autores vivos en determinado año
                        
                        0 - Salir
                    """;
            System.out.println(menu);
            try {
                System.out.print("Seleccione una opcion: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroWeb();
                        break;
                    case 2:
                        mostrarLibrosBuscados();
                        break;
                    case 3:
                        mostrarLibrosPorIdioma();
                        break;
                    case 4:
                        mostrarListaDeAutores();
                        break;
                    case 5:
                        mostrarListaDeAutoresVivosEnDeterminadoAnio();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicacion...");
                        break;
                    default:
                        System.out.println("Opción invalida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no valida. Por favor, ingrese un numero.");
                scanner.nextLine();
            }
        }
    }

    private DatosLibro getDatosLibro(){
        System.out.print("Escriba el nombre del libro que desea buscar: ");
        var nombreLibro = scanner.nextLine();
        try {
            var json = consumoAPI.obtenerDatos(URL_BASE+URLEncoder
                    .encode(nombreLibro, "UTF-8"));
            ResultadoDeLibros datos = conversor
                    .obtenerDatos(json, ResultadoDeLibros.class);
            if (datos.libros() != null && !datos.libros().isEmpty()) {
                return datos.libros().get(0);
            } else {
                System.out.println("No se encontraron libros para la busqueda.");
                return null;
            }

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al codificar la URL", e);
        }
    }
    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibro();
        if (libroRepository.existsByTitulo(datos.titulo())) {
            System.out.println("El libro ya existe en la base de datos.");
        } else if (datos != null && !libroRepository.existsByTitulo(datos.titulo())) {
            Libro libro = new Libro(datos);

            Autor autorExistente = autorRepository.findByNombreAndFechaNacimientoAndFechaMuerte(
                    libro.getAutor().getNombre(),
                    libro.getAutor().getFechaNacimiento(),
                    libro.getAutor().getFechaMuerte()
            );

            if (autorExistente != null) {
                libro.setAutor(autorExistente);
                libroRepository.save(libro);
                System.out.println("Libro guardado: " + libro.getTitulo());
            } else {
                autorRepository.save(libro.getAutor());
                libroRepository.save(libro);
                System.out.println("Libro guardado: " + libro.getTitulo());
            }
        } else {
            System.out.println("No se pudo guardar el libro porque no se encontraron resultados.");
        }
    }

    private void mostrarLibrosBuscados(){
        libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    private void mostrarLibrosPorIdioma(){
        System.out.println("""
            Escriba el idioma por el que quiera buscar el libro
            (solo se acepta 'Ingles' o 'Español')
        """);
        var idiomaLibro = scanner.nextLine();
        var idioma = Idioma.fromNormal(idiomaLibro);
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (idiomaLibro.equalsIgnoreCase(String.valueOf(Idioma.INGLES)) || idiomaLibro.equalsIgnoreCase(String.valueOf(Idioma.ESPAÑOL))) {
            System.out.println("Lista de libros en el idioma "+idiomaLibro);
            librosPorIdioma.forEach(System.out::println);
        } else {
            System.out.println("Idioma mal escrito.");
        }
    }

    private void mostrarListaDeAutores() {
        autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    private void mostrarListaDeAutoresVivosEnDeterminadoAnio() {
        System.out.print("Ingrese el año: ");
        var anio = scanner.nextInt();
        autores = autorRepository.vivo(anio);
        System.out.println("Autores vivos en el año "+anio);
        autores.forEach(System.out::println);
    }
}
