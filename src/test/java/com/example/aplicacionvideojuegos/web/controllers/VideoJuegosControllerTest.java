package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.users.services.UserService;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class VideoJuegosControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private VideoJuegoService  videoJuegoService;

    @MockitoBean
    private UserService userService;

    private final VideoJuegos juego1 = VideoJuegos.builder()
            .id(1L)
            .nombre("gta v")
            .precio(59.99)
            .fecha_lanzamiento(LocalDate.of(2012,5,14))
            .genero("Accion")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private final VideoJuegos juego2 = VideoJuegos.builder()
            .id(1L)
            .nombre("fifa 15")
            .precio(89.99)
            .fecha_lanzamiento(LocalDate.of(2015,9,21))
            .genero("Deportes")
            .plataforma(VideoJuegos.Plataforma.PS4)
            .edad(3)
            .build();

    // ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE AL PEDIR UN VIDEOJUEGO POR ID
// SE DEVUELVE LA VISTA DETALLE CON LOS DATOS CORRECTOS
// ======================================================
// ✅ Devuelve la vista detalle con el videojuego encontrado
    @WithUserDetails("patrickU") // 👈 Usa un usuario REAL que debe existir en data.sql
    @Test
    @DisplayName("GET /app/misVideoJuegos/{id} - Devuelve detalle del juego con {id}")
    void getById(){

        Long id = 1L; // 👈 ID del videojuego que vamos a consultar

        // 1️⃣ ARRANGE
        when(videoJuegoService.buscarPorId(id)) // 👈 Simulamos que el service encuentra el juego
                .thenReturn(Optional.of(juego1)); // 👈 Devuelve juego1

        // 2️⃣ ACT
        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/app/misVideoJuegos/{id}", id) // 👈 Endpoint con variable path {id}
                .contentType(MediaType.TEXT_HTML) // 👈 Esperamos HTML (vista MVC)
                .exchange(); // 👈 Ejecutamos la petición

        // 3️⃣ ASSERT
        var mvcAssert = assertThat(result)
                .hasStatusOk() // 👈 Comprobamos que devuelve 200 OK
                .hasViewName("app/videojuegos/detalle"); // 👈 Comprobamos que carga la vista correcta

        mvcAssert.model()
                .containsKeys("videojuego") // 👈 El modelo debe contener atributo "videojuego"
                .containsEntry("videojuego", juego1); // 👈 Y debe ser el objeto esperado

        mvcAssert.bodyText()
                .contains("Detalles del videojuego " + id); // 👈 El HTML debe contener el título correcto

        // 4️⃣ VERIFY
        verify(videoJuegoService, only()) // 👈 Verificamos que solo se llamó a este método
                .buscarPorId(anyLong());
    }

    // ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SE DEVUELVE LA LISTA
// DE VIDEOJUEGOS DEL USUARIO AUTENTICADO
// ======================================================
// ✅ Devuelve la vista lista con los videojuegos del usuario activo
    @WithUserDetails("patrickU") // 👈 Usuario real que debe existir en data.sql
    @Test
    @DisplayName("GET /app/misVideoJuegos - Devuelve lista de videojuegos del usuario activo")
    void misVideoJuegos() {

        Long usuarioId = 4L; // 👈 ID del usuario autenticado (ajústalo si es distinto en tu data.sql)

        var usuario = com.example.aplicacionvideojuegos.rest.users.models.User.builder()
                .id(usuarioId)
                .username("patrickU")
                .build(); // 👈 Usuario mínimo necesario para el test

        var dto1 = new VideoJuegosResponseDto();
        var dto2 = new VideoJuegosResponseDto(); // 👈 Lista simulada con 2 dto

        Page<VideoJuegosResponseDto> page = new PageImpl<>(List.of(dto1, dto2));

        // 1️⃣ ARRANGE
        when(userService.findByUsername(anyString())) // 👈 Simulamos búsqueda del usuario
                .thenReturn(Optional.of(usuario));

        when(videoJuegoService.findByUsuarioId(anyLong(), any(Pageable.class))) // 👈 Simulamos videojuegos del usuario
                .thenReturn(page);

        // 2️⃣ ACT
        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/app/misVideoJuegos") // 👈 Endpoint listado
                .contentType(MediaType.TEXT_HTML)
                .exchange(); // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatusOk() // 👈 Esperamos 200 OK
                .hasViewName("app/videojuegos/lista") // 👈 Vista correcta
                .model()
                .containsKeys("page") // 👈 El modelo debe contener "videojuegos"
                .hasEntrySatisfying("page", p ->
                        assertThat((Page<?>) p) // 👈 Comprobamos que es lista
                                .isInstanceOf(Page.class)
                                .hasSize(2)); // 👈 Y tiene 2 elementos

        // 4️⃣ VERIFY
        verify(userService, times(1))
                .findByUsername(anyString()); // 👈 Se llamó al servicio de usuario

        verify(videoJuegoService, times(1))
                .findByUsuarioId(anyLong(), any(Pageable.class)); // 👈 Se llamó al servicio de videojuegos
    }
}
