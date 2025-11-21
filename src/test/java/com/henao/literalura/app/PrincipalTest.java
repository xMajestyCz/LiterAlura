package com.henao.literalura.app;

import com.henao.literalura.model.*;
import com.henao.literalura.repository.AutorRepository;
import com.henao.literalura.repository.LibroRepository;
import com.henao.literalura.service.ConsumoAPI;
import com.henao.literalura.service.ConvierteDatos;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PrincipalTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private AutorRepository autorRepository;

    private Principal principal;
    private AutoCloseable mocks;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() throws Exception {
        System.setIn(System.in);
        System.setOut(System.out);
        mocks.close();
    }

    private void setScannerInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void testMenuMuestraOpciones() {
        setScannerInput("0\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class)) {
            
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarMenu();
            String salida = output.toString();
            assertTrue(salida.contains("1 - Buscar Libro"));
            assertTrue(salida.contains("Cerrando la aplicación"));
        }
    }

    @Test
    void testMostrarLibros() {
        setScannerInput("2\n0\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class)) {
            
            when(libroRepository.findAll()).thenReturn(List.of());
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarMenu();
            verify(libroRepository, times(1)).findAll();
        }
    }

    @Test
    void testMostrarLibrosPorIdioma() {
        setScannerInput("3\nEspañol\n0\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class)) {
            
            when(libroRepository.findByIdioma(Idioma.ESPAÑOL)).thenReturn(List.of());
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarMenu();
            String salida = output.toString();
            assertTrue(salida.contains("Lista de libros en el idioma Español"));
            verify(libroRepository, times(1)).findByIdioma(Idioma.ESPAÑOL);
        }
    }

    @Test
    void testMostrarAutores() {
        setScannerInput("4\n0\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class)) {
            
            when(autorRepository.findAll()).thenReturn(List.of());
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarMenu();
            verify(autorRepository, times(1)).findAll();
        }
    }

    @Test
    void testOpcionInvalida() {
        setScannerInput("99\n0\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class)) {
            
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarMenu();
            String salida = output.toString();
            assertTrue(salida.contains("Opción inválida"));
        }
    }

    @Test
    void testGetDatosLibro_NombreVacio() {
        setScannerInput("\n"); // Entrada vacía
        try (MockedConstruction<ConsumoAPI> ignored1 = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> ignored2 = mockConstruction(ConvierteDatos.class)) {
            
            principal = new Principal(libroRepository, autorRepository);
            DatosLibro resultado = principal.getDatosLibro();
            
            assertNull(resultado);
            String salida = output.toString();
            assertTrue(salida.contains("El nombre del libro no puede estar vacío"));
        }
    }

    @Test
    void testGetDatosLibro_NoEncuentraLibros() throws UnsupportedEncodingException {
        setScannerInput("Libro Inexistente\n");
        try (MockedConstruction<ConsumoAPI> mockedConsumo = mockConstruction(ConsumoAPI.class, 
                (mock, context) -> when(mock.obtenerDatos(anyString())).thenReturn("{\"results\":[]}"));
             MockedConstruction<ConvierteDatos> mockedConversor = mockConstruction(ConvierteDatos.class,
                (mock, context) -> when(mock.obtenerDatos(anyString(), eq(ResultadoDeLibros.class)))
                    .thenReturn(new ResultadoDeLibros(List.of())))) {
            
            principal = new Principal(libroRepository, autorRepository);
            DatosLibro resultado = principal.getDatosLibro();
            
            assertNull(resultado);
            String salida = output.toString();
            assertTrue(salida.contains("No se encontraron libros para la búsqueda"));
        }
    }

    @Test
    void testMostrarLibrosPorIdioma_Invalido() {
        setScannerInput("IdiomaInvalido\n");
        try (MockedConstruction<ConsumoAPI> ignored1 = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> ignored2 = mockConstruction(ConvierteDatos.class)) {
            
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarLibrosPorIdioma();
            
            String salida = output.toString();
            assertTrue(salida.contains("Idioma mal escrito"));
            verify(libroRepository, never()).findByIdioma(any());
        }
    }

    @Test
    void testMostrarAutoresVivos_AnioInvalido() {
        setScannerInput("abc\n");
        try (MockedConstruction<ConsumoAPI> ignored1 = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> ignored2 = mockConstruction(ConvierteDatos.class)) {
            
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarListaDeAutoresVivosEnDeterminadoAnio();
            
            String salida = output.toString();
            assertTrue(salida.contains("Debe ingresar un año válido"));
            verify(autorRepository, never()).vivo(anyInt());
        }
    }

    @Test
    void testMostrarAutoresVivos_AnioValido() {
        setScannerInput("2020\n");
        try (MockedConstruction<ConsumoAPI> ignored1 = mockConstruction(ConsumoAPI.class);
             MockedConstruction<ConvierteDatos> ignored2 = mockConstruction(ConvierteDatos.class)) {
            
            when(autorRepository.vivo(2020)).thenReturn(List.of());
            
            principal = new Principal(libroRepository, autorRepository);
            principal.mostrarListaDeAutoresVivosEnDeterminadoAnio();
            
            String salida = output.toString();
            assertTrue(salida.contains("Autores vivos en el año 2020"));
            verify(autorRepository, times(1)).vivo(2020);
        }
    }
}