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

@Slf4j
@SpringBootTest()
@AutoConfigureMockMvc
class VideoJuegosControllerTest {

    private final String ENDPOINT = "/api/v1/videoJuegos";

    private final VideoJuegosResponseDto videoJuegosResponse1 = VideoJuegosResponseDto.builder()
            .id(1L)
            .nombre("GTA VI")
            .precio(120.0)
            .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
            .genero("Acción")
            .plataforma( VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private final VideoJuegosResponseDto videoJuegosResponse2 = VideoJuegosResponseDto.builder()
            .id(2L)
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
        var videoJuegosResponses = List.of(videoJuegosResponse1, videoJuegosResponse2);
        when (
                videoJuegoService
                .findAll(null, null,null))
                .thenReturn(videoJuegosResponses
            );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        verify(videoJuegoService, times(1)).findAll(null, null, null);

    }

    @Test
    void getAllByNombre() {
        log.info("obtener todos los videojuegos filtrando por nombre");
        var videoJuegosResponses = List.of(videoJuegosResponse2);
        String queryString = "?nombre=" + videoJuegosResponse2.getNombre();

        when (
                videoJuegoService
                        .findAll(anyString(), isNull(),isNull()))
                .thenReturn(videoJuegosResponses
                );
        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        verify(videoJuegoService, times(1)).findAll(anyString(), isNull(),isNull());

    }

    @Test
    void getAllByGenero() {
        log.info("obtener todos los videojuegos filtrando por genero");
        var videoJuegosResponses = List.of(videoJuegosResponse1);
        String queryString = "?genero=" + videoJuegosResponse1.getGenero();

        when (
                videoJuegoService
                        .findAll(isNull(), anyString(),isNull()))
                .thenReturn(videoJuegosResponses
                );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                });

        verify(videoJuegoService, times(1)).findAll(isNull(), anyString(),isNull());

    }

    @Test
    void getAllByPlataforma() {
        log.info("obtener todos los videojuegos filtrando por plataforma");
        var videoJuegosResponses = List.of(videoJuegosResponse1, videoJuegosResponse2);
        String queryString = "?plataforma=" + videoJuegosResponse1.getPlataforma();

        when (
                videoJuegoService
                        .findAll(isNull(), isNull(),any(VideoJuegos.Plataforma.class)))
                .thenReturn(videoJuegosResponses
                );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                    assertThat(json).extractingPath("$[1]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        verify(videoJuegoService, times(1)).findAll(isNull(), isNull(),any(VideoJuegos.Plataforma.class));
    }

    @Test
    void getAllByNombreAndGenero() {
        log.info("obtener todos los videojuegos filtrando por nombre y genero");
        var videoJuegosResponses = List.of(videoJuegosResponse2);
        String queryString = "?nombre=" + videoJuegosResponse2.getNombre() +
                "&genero=" + videoJuegosResponse2.getGenero();

        when (
                videoJuegoService
                        .findAll(anyString(), anyString(),isNull()))
                .thenReturn(videoJuegosResponses
                );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse2);
                });

        verify(videoJuegoService, times(1)).findAll(anyString(), anyString(),isNull());
    }

    @Test
    void getAllByNombreAndPlataforma() {
        log.info("obtener todos los videojuegos filtrando por nombre y plataforma");
        var videoJuegosResponses = List.of(videoJuegosResponse1);
        String queryString = "?nombre=" + videoJuegosResponse1.getNombre() +
                "&plataforma=" + videoJuegosResponse1.getPlataforma();

        when (
                videoJuegoService
                        .findAll(anyString(), isNull(),any(VideoJuegos.Plataforma.class)))
                .thenReturn(videoJuegosResponses
                );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                });

        verify(videoJuegoService, times(1)).findAll(anyString(), isNull(),any(VideoJuegos.Plataforma.class));
    }

    @Test
    void getAllByGeneroAndPlataforma() {
        log.info("obtener todos los videojuegos filtrando por genero y plataforma");
        var videoJuegosResponses = List.of(videoJuegosResponse1);
        String queryString = "?genero=" + videoJuegosResponse1.getGenero() +
                "&plataforma=" + videoJuegosResponse1.getPlataforma();

        when (
                videoJuegoService
                        .findAll(isNull(), anyString(),any(VideoJuegos.Plataforma.class)))
                .thenReturn(videoJuegosResponses
                );

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.length()").isEqualTo(videoJuegosResponses.size());
                    assertThat(json).extractingPath("$[0]")
                            .convertTo(VideoJuegosResponseDto.class).isEqualTo(videoJuegosResponse1);
                });

        verify(videoJuegoService, times(1)).findAll(isNull(), anyString(),any(VideoJuegos.Plataforma.class));
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
    void getVideoJuegoByIdConIdValido() {
        log.info("obtener un videojuego por id pasando un id valido");
        Long id = 1L;

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
    void create() {
        log.info("crear un nuevo videojuego");

        String requestBody = """
                {
                    "nombre": "Elden Ring",
                    "precio": 59.99,
                    "fecha_lanzamiento": "2022-02-25",
                    "genero": "RPG",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        var juegoSaved = VideoJuegosResponseDto.builder()
                .id(3L)
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

        assertThat(resultado)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(juegoSaved);

        verify(videoJuegoService, times(1)).save(any(VideoJuegosCreateDto.class));

    }

    @Test
    void createConDatosInvalidos() {
        log.info("crear un nuevo videojuego con datos invalidos");
        String requestBody = """
                {
                    "nombre": "",
                    "precio": -10,
                    "fecha_lanzamiento": "2025-12-31",
                    "genero": "",
                    "plataforma": "PS5",
                    "edad": -5
                }
                """;
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
                    "precio": 130.0,
                    "fecha_lanzamiento": "2026-05-26",
                    "genero": "Acción",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        var juegoUpdated = VideoJuegosResponseDto.builder()
                .id(1L)
                .nombre("GTA VI")
                .precio(130.0)
                .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(18)
                .build();

        when(videoJuegoService.update(anyLong(), any(VideoJuegosUpdateDto.class)))
                .thenReturn(juegoUpdated);

        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(VideoJuegosResponseDto.class)
                .isEqualTo(juegoUpdated);

        verify(videoJuegoService, only()).update(anyLong(), any(VideoJuegosUpdateDto.class));
    }

    @Test
    void noUpdateConIdInvalidos() {
        log.info("No actualizar un videojuego con id invalido");
        Long id = 12L;
        String requestBody = """
                {
                    "nombre": "GTA VI",
                    "precio": 130.0,
                    "fecha_lanzamiento": "2026-05-26",
                    "genero": "Acción",
                    "plataforma": "PS5",
                    "edad": 18
                }
                """;

        when(videoJuegoService.update(anyLong(), any(VideoJuegosUpdateDto.class)))
                .thenThrow(new VideoJuegosNotFound(id));

        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessageContaining("no encontrada");

        verify(videoJuegoService, only()).update(anyLong(), any());

    }

    @Test
    void deleteConIdValido() {
        log.info("eliminar un videojuego con id valido");
        Long id = 2L;

        doNothing().when(videoJuegoService).deleteById(anyLong());

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
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessageContaining("no encontrada");

        verify(videoJuegoService, only()).deleteById(anyLong());
    }


}