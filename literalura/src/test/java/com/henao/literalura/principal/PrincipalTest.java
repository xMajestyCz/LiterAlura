package com.henao.literalura.principal;

import com.henao.literalura.model.*;
import com.henao.literalura.repository.AutorRepository;
import com.henao.literalura.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrincipalTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private AutorRepository autorRepository;

    @InjectMocks
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = new Principal(libroRepository, autorRepository);
    }

    // Caso 1: Cuando el libro ya existe en la base
    @Test
    void testLibroYaExiste() {
        when(libroRepository.existsByTitulo("Don Quijote")).thenReturn(true);

        boolean existe = libroRepository.existsByTitulo("Don Quijote");

        assertTrue(existe);
        verify(libroRepository, times(1)).existsByTitulo("Don Quijote");
        verify(libroRepository, never()).save(any(Libro.class));
    }

    // Caso 2: Guardar un libro nuevo y un autor nuevo
    @Test
    void testGuardarLibroNuevoYAutorNuevo() {
        // Datos del autor
        Autor autor = new Autor();
        autor.setNombre("Miguel de Cervantes");
        autor.setFechaNacimiento(1547);
        autor.setFechaMuerte(1616);

        // Datos del libro
        Libro libro = new Libro();
        libro.setTitulo("Don Quijote");
        libro.setAutor(autor);
        libro.setIdioma(Idioma.ESPAÑOL);
        libro.setNumeroDeDescargas(500);

        when(libroRepository.existsByTitulo("Don Quijote")).thenReturn(false);
        when(autorRepository.findByNombreAndFechaNacimientoAndFechaMuerte(
                "Miguel de Cervantes", 1547, 1616)).thenReturn(null);

        autorRepository.save(autor);
        libroRepository.save(libro);

        verify(autorRepository, times(1)).save(autor);
        verify(libroRepository, times(1)).save(libro);
    }

    // Caso 3: Mostrar lista de libros buscados
    @Test
    void testMostrarLibrosBuscados() {
        Autor autor = new Autor();
        autor.setNombre("Gabriel García Márquez");
        Libro libro = new Libro();
        libro.setTitulo("Cien años de soledad");
        libro.setAutor(autor);
        libro.setIdioma(Idioma.ESPAÑOL);

        when(libroRepository.findAll()).thenReturn(List.of(libro));

        List<Libro> libros = libroRepository.findAll();

        assertEquals(1, libros.size());
        assertEquals("Cien años de soledad", libros.get(0).getTitulo());
    }

    //Caso 4: Filtrar libros por idioma (Español)
    @Test
    void testMostrarLibrosPorIdiomaEspanol() {
        Autor autor = new Autor();
        autor.setNombre("Antoine de Saint-Exupéry");

        Libro libro = new Libro();
        libro.setTitulo("El Principito");
        libro.setAutor(autor);
        libro.setIdioma(Idioma.ESPAÑOL);

        when(libroRepository.findByIdioma(Idioma.ESPAÑOL)).thenReturn(List.of(libro));

        List<Libro> libros = libroRepository.findByIdioma(Idioma.ESPAÑOL);

        assertEquals(1, libros.size());
        assertEquals(Idioma.ESPAÑOL, libros.get(0).getIdioma());
        assertEquals("El Principito", libros.get(0).getTitulo());
    }

    // Caso 5: Buscar autores vivos en determinado año
    @Test
    void testAutoresVivosEnDeterminadoAnio() {
        Autor autor1 = new Autor();
        autor1.setNombre("Gabriel García Márquez");
        autor1.setFechaNacimiento(1927);
        autor1.setFechaMuerte(2014);

        Autor autor2 = new Autor();
        autor2.setNombre("Isabel Allende");
        autor2.setFechaNacimiento(1942);
        autor2.setFechaMuerte(null);

        when(autorRepository.vivo(1980)).thenReturn(List.of(autor1, autor2));

        List<Autor> autores = autorRepository.vivo(1980);

        assertEquals(2, autores.size());
        assertTrue(autores.stream().anyMatch(a -> a.getNombre().equals("Gabriel García Márquez")));
        assertTrue(autores.stream().anyMatch(a -> a.getNombre().equals("Isabel Allende")));
    }
}
