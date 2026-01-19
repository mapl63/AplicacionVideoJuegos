package com.example.aplicacionvideojuegos.rest.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class VideoJuegosRestControllerTest {


    private final String ENDPOINT = "/api/v1/videoJuegos";


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


    @Autowired
    private MockMvcTester mockMvcTester;


    @MockitoBean
    private VideoJuegoService videoJuegoService;

    @Test
    void getAllVideoJuegos() {
        log.info("obtener todos los videojuegos sin pasar ningun parametro de busqueda");
        // Arrange: el servicio devuelve los dos videojuegos de ejemplo
        var videoJuegosResponses = List.of(videoJuegosResponse1, videoJuegosResponse2);

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(videoJuegosResponses);

        when(videoJuegoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        var result = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
            .hasStatusOk()
            .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
            });

        verify(videoJuegoService, times(1))
            .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    void getAllByNombre() {
        log.info("obtener todos los videojuegos filtrando por nombre");

        var videoJuegosResponses = List.of(videoJuegosResponse2);

        String queryString = "?nombre=" + videoJuegosResponse2.getNombre();

        Optional<String> nombre = Optional.of(videoJuegosResponse2.getNombre());

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(videoJuegosResponses);

        when (videoJuegoService
                .findAll(nombre, Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page);

        // Act: GET con el parámetro nombre
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: se devuelve solo el resultado esperado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        // Verify: el servicio se invoca con el nombre y cliente nulo
        verify(videoJuegoService, times(1)).findAll(nombre, Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    void getAllByCliente(){
        log.info("obtener todos los videojuegos filtrando por cliente");

        var videoJuegosResponses = List.of(videoJuegosResponse1);

        String queryString = "?cliente=" + videoJuegosResponse1.getCliente();

        Optional<String> cliente = Optional.of(videoJuegosResponse1.getCliente());

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(videoJuegosResponses);

        when (videoJuegoService
                .findAll(Optional.empty(), cliente, Optional.empty(), pageable))
                .thenReturn(page);

        // Act: GET con parámetro cliente
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: únicamente llega el videojuego asociado a ese cliente
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);

                });


        verify(videoJuegoService, only()).findAll(Optional.empty(), cliente, Optional.empty(), pageable);
    }

    @Test
    void getAllByNombreAndCliente(){
        log.info("obtener todos los videojuegos filtrando por nombre y cliente");

        var videoJuegosResponses = List.of(videoJuegosResponse1);

        String queryString = "?nombre=" + videoJuegosResponse1.getNombre() +
                "&cliente=" + videoJuegosResponse1.getCliente();

        Optional<String> nombre = Optional.of(videoJuegosResponse1.getNombre());
        Optional<String> cliente = Optional.of(videoJuegosResponse1.getCliente());

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(videoJuegosResponses);

        when (videoJuegoService
                .findAll(nombre, cliente, Optional.empty(), pageable))
                .thenReturn(page);


        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();


        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                });


        verify(videoJuegoService, only()).findAll(nombre, cliente, Optional.empty(), pageable);
    }

    @Test
    void getVideoJuegoByIdConIdValido() {
        log.info("obtener un videojuego por id pasando un id valido");

        Long id = videoJuegosResponse1.getId();
        when(videoJuegoService.findById(anyLong()))
                .thenReturn(videoJuegosResponse1);


        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(videoJuegosResponse1);

        verify(videoJuegoService, only()).findById(anyLong());
    }

    @Test
    void getByIdConIdInvalido(){
        log.info("obtener un videojuego por id pasando un id invalido");

        Long id = 9L;
        when(videoJuegoService.findById(anyLong()))
                .thenThrow(new VideoJuegosNotFound(id));

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatus4xxClientError()
                .hasFailed().failure()
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessageContaining("no encontrada");

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


        when(videoJuegoService.save(any(VideoJuegosCreateDto.class)))
                .thenReturn(juegoSaved);

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
        doNothing().when(videoJuegoService).deleteById(anyLong());

        // Act: enviamos DELETE /{id}
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(videoJuegoService, only()).deleteById(anyLong());
    }

    @Test
    void deleteConIdInvalido() {
        log.info("eliminar un videojuego con id invalido");

        Long id = 15L;
        doThrow(new VideoJuegosNotFound(id))
                .when(videoJuegoService).deleteById(anyLong());

        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND);

        verify(videoJuegoService, only()).deleteById(anyLong());
    }


}