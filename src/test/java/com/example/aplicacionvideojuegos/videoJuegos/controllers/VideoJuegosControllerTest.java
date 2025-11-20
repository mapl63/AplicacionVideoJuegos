package com.example.aplicacionvideojuegos.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.services.VideoJuegoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración del controlador REST de videojuegos.
 * Usa {@link MockMvcTester} para simular las peticiones HTTP y Mockito para
 * definir la capa de servicio.
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class VideoJuegosControllerTest {

    /**
     * Endpoint base del recurso videojuegos.
     */
    private final String ENDPOINT = "/api/v1/videoJuegos";

    /**
     * Respuesta de ejemplo asociada al primer videojuego.
     */
    private final VideoJuegosResponseDto videoJuegosResponse1 = VideoJuegosResponseDto.builder()
            .id(1L)
            .cliente("juan")
            .nombre("GTA VI")
            .precio(120.0)
            .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
            .genero("Acción")
            .plataforma( VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    /**
     * Respuesta de ejemplo asociada al segundo videojuego.
     */
    private final VideoJuegosResponseDto videoJuegosResponse2 = VideoJuegosResponseDto.builder()
            .id(2L)
            .cliente("maria")
            .nombre("The Witcher 4")
            .precio(89.99)
            .fecha_lanzamiento(LocalDate.of(2027, 7, 24))
            .genero("RPG")
            .plataforma( VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    /**
     * Tester HTTP inyectado por Spring.
     */
    @Autowired
    private MockMvcTester mockMvcTester;

    /**
     * Servicio mockeado para aislar el controlador.
     */
    @MockitoBean
    private VideoJuegoService videoJuegoService;

    @Test
    void getAllVideoJuegos() {
        log.info("obtener todos los videojuegos sin pasar ningun parametro de busqueda");
        // Arrange: el servicio devuelve los dos videojuegos de ejemplo
        var videoJuegosResponses = List.of(videoJuegosResponse1, videoJuegosResponse2);
        when (
                videoJuegoService
                .findAll(null, null))
                .thenReturn(videoJuegosResponses
            );

        // Act: petición GET sin filtros
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: respuesta 200 con la lista exacta
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        // Verify: el servicio solo se consulta una vez sin filtros
        verify(videoJuegoService, times(1)).findAll(null, null);

    }

    @Test
    void getAllByNombre() {
        log.info("obtener todos los videojuegos filtrando por nombre");
        // Arrange: el servicio devuelve solo el videojuego con el nombre solicitado
        var videoJuegosResponses = List.of(videoJuegosResponse2);
        String queryString = "?nombre=" + videoJuegosResponse2.getNombre();

        when (videoJuegoService
                .findAll(anyString(), isNull()))
                .thenReturn(videoJuegosResponses);

        // Act: GET con el parámetro nombre
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: se devuelve solo el resultado esperado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        // Verify: el servicio se invoca con el nombre y cliente nulo
        verify(videoJuegoService, times(1)).findAll(anyString(), isNull());

    }

    @Test
    void getAllByCliente(){
        log.info("obtener todos los videojuegos filtrando por cliente");
        // Arrange: el filtro por cliente debe devolver solo el primer videojuego
        var videoJuegosResponses = List.of(videoJuegosResponse1);
        String queryString = "?cliente=" + videoJuegosResponse1.getCliente();
        when (videoJuegoService
                .findAll(isNull(), anyString()))
                .thenReturn(videoJuegosResponses);

        // Act: GET con parámetro cliente
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: únicamente llega el videojuego asociado a ese cliente
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);

                });

        // Verify: se invoca al servicio con nombre nulo y cliente informado
        verify(videoJuegoService, only()).findAll(isNull(), anyString());
    }

    @Test
    void getAllByNombreAndCliente(){
        log.info("obtener todos los videojuegos filtrando por nombre y cliente");
        // Arrange: la búsqueda combinada debe devolver una única coincidencia
        var videoJuegosResponses = List.of(videoJuegosResponse1);
        String queryString = "?nombre=" + videoJuegosResponse1.getNombre() +
                "&cliente=" + videoJuegosResponse1.getCliente();

        when (videoJuegoService
                .findAll(anyString(), anyString()))
                .thenReturn(videoJuegosResponses);

        // Act: GET con ambos query params
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: la respuesta contiene la lista esperada
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                });

        // Verify: solo se hace una llamada al servicio con ambos filtros
        verify(videoJuegoService, only()).findAll(anyString(), anyString());
    }

    @Test
    void getVideoJuegoByIdConIdValido() {
        log.info("obtener un videojuego por id pasando un id valido");

        Long id = videoJuegosResponse1.getId();
        // Arrange: el servicio devuelve la respuesta asociada al ID
        when(videoJuegoService.findById(anyLong()))
                .thenReturn(videoJuegosResponse1);

        // Act: ejecutamos GET /{id}
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: se obtiene 200 y el cuerpo esperado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(videoJuegosResponse1);

        // Verify: el servicio se invoca una sola vez
        verify(videoJuegoService, only()).findById(anyLong());
    }

    @Test
    void getByIdConIdInvalido(){
        log.info("obtener un videojuego por id pasando un id invalido");
        Long id = 9L;

        // Arrange: el servicio lanza VideoJuegosNotFound para el ID indicado
        when(videoJuegoService.findById(anyLong()))
                .thenThrow(new VideoJuegosNotFound(id));

        // Act: GET con un ID inexistente
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: el controlador responde 404
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND);

        // Verify: el servicio se invoca una sola vez con el ID
        verify(videoJuegoService, only()).findById(anyLong());

    }

    @Test
    void create() {
        log.info("crear un nuevo videojuego");

        String requestBody = """
                {
                    "nombre": "Elden Ring",
                    "cliente": "carlos",
                    "precio": 59.99,
                    "fecha_lanzamiento": "2022-02-25",
                    "genero": "RPG",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        var juegoSaved = VideoJuegosResponseDto.builder()
                .id(3L)
                .cliente("carlos")
                .nombre("Elden Ring")
                .precio(59.99)
                .fecha_lanzamiento(LocalDate.of(2022, 2, 25))
                .genero("RPG")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(18)
                .build();

        // Arrange: el servicio devuelve la respuesta del videojuego creado
        when(videoJuegoService.save(any(VideoJuegosCreateDto.class)))
                .thenReturn(juegoSaved);

        // Act: petición POST creando el videojuego
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: se devuelve 201 con el cuerpo correcto
        assertThat(resultado)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(juegoSaved);

        // Verify: el servicio se invoca exactamente una vez
        verify(videoJuegoService, times(1)).save(any(VideoJuegosCreateDto.class));

    }

    @Test
    void createConDatosInvalidos() {
        log.info("crear un nuevo videojuego con datos invalidos");
        String requestBody = """
                {
                    "nombre": "",
                    "cliente": "",
                    "precio": -10,
                    "fecha_lanzamiento": "2025-12-31",
                    "genero": "",
                    "plataforma": "PS5",
                    "edad": -5
                }
                """;
        // Act: enviamos un cuerpo inválido
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: se obtiene un 400 con detalle de errores de validación
        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", errores -> {
                    assertThat(errores).hasFieldOrProperty("nombre");
                    assertThat(errores).hasFieldOrProperty("cliente");
                    assertThat(errores).hasFieldOrProperty("precio");
                    assertThat(errores).hasFieldOrProperty("edad");
                    assertThat(errores).hasFieldOrProperty("genero");
                });

        // Verify: el servicio no se llama cuando la petición es inválida
        verify(videoJuegoService, never()).save(any(VideoJuegosCreateDto.class));

    }

    @Test
    void update() {
        log.info("actualizar un videojuego existente");

        Long id = 1L;
        String requestBody = """
                {
                    "nombre": "GTA VI",
                    "cliente": "juan",
                    "precio": 130.0,
                    "fecha_lanzamiento": "2026-05-26",
                    "genero": "Acción",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        var juegoUpdated = VideoJuegosResponseDto.builder()
                .id(1L)
                .cliente("juan")
                .nombre("GTA VI")
                .precio(130.0)
                .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(18)
                .build();

        // Arrange: el servicio devuelve el videojuego ya actualizado
        when(videoJuegoService.update(anyLong(), any(VideoJuegosUpdateDto.class)))
                .thenReturn(juegoUpdated);

        // Act: enviamos la petición PUT con el nuevo contenido
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: respuesta 200 con el DTO actualizado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(juegoUpdated);

        // Verify: solo una llamada al servicio para la actualización
        verify(videoJuegoService, only()).update(anyLong(), any(VideoJuegosUpdateDto.class));
    }

    @Test
    void noUpdateConIdInvalidos() {
        log.info("No actualizar un videojuego con id invalido");

        Long id = 12L;
        String requestBody = """
                {
                    "cliente": "juan",
                    "nombre": "GTA VI",
                    "precio": 130.0,
                    "fecha_lanzamiento": "2026-05-26",
                    "genero": "Acción",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        // Arrange: el servicio lanza not found al intentar actualizar
        when(videoJuegoService.update(anyLong(), any(VideoJuegosUpdateDto.class)))
                .thenThrow(new VideoJuegosNotFound(id));

        // Act: petición PUT con un ID inválido
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: se obtiene 404
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND);

        // Verify: solo se realiza una llamada al servicio
        verify(videoJuegoService, only()).update(anyLong(), any());

    }

    @Test
    void deleteConIdValido() {
        log.info("eliminar un videojuego con id valido");

        Long id = 2L;
        // Arrange: la capa de servicio no lanza errores al borrar
        doNothing().when(videoJuegoService).deleteById(anyLong());

        // Act: enviamos DELETE /{id}
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert: se recibe 204 No Content
        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        // Verify: solo se invoca una vez al servicio
        verify(videoJuegoService, only()).deleteById(anyLong());
    }

    @Test
    void deleteConIdInvalido() {
        log.info("eliminar un videojuego con id invalido");

        Long id = 15L;
        // Arrange: eliminar con ese ID lanza VideoJuegosNotFound
        doThrow(new VideoJuegosNotFound(id))
                .when(videoJuegoService).deleteById(anyLong());

        // Act: petición DELETE con ID inexistente
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        // Assert: el controlador devuelve 404
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND);

        // Verify: se invoca una única vez al servicio
        verify(videoJuegoService, only()).deleteById(anyLong());
    }


}