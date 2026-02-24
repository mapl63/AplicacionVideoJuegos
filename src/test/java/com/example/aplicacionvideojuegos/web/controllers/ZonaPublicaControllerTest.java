package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class ZonaPublicaControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private VideoJuegoService videoJuegoService;

    // ======================================================
// 🔵 TEST index()
// ======================================================
// ✅ Devuelve página pública con videojuegos paginados
    @Test
    @DisplayName("GET /public devuelve index con página de videojuegos")
    void index() {

        VideoJuegosResponseDto dto = VideoJuegosResponseDto.builder()
                .id(1L)
                .nombre("GTA")
                .build();

        Page<VideoJuegosResponseDto> page =
                new PageImpl<>(List.of(dto),
                        PageRequest.of(0, 4, Sort.by("id").ascending()),
                        1);

        when(videoJuegoService.findAll(any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        var result = mockMvcTester.get()
                .uri("/public")
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("index")
                .model()
                .containsKeys("page");

        verify(videoJuegoService, times(1))
                .findAll(any(), any(), any(), any(Pageable.class));
    }

    // ======================================================
// 🔵 TEST setLang() idioma válido
// ======================================================
// ✅ Crea cookie y redirige al referer
    @Test
    @DisplayName("GET /public/set-lang con idioma válido crea cookie y redirige")
    void setLangValid() {

        var result = mockMvcTester.get()
                .uri("/public/set-lang?lang=es")
                .header("Referer", "/public/index")
                .exchange();

        assertThat(result)
                .hasStatus3xxRedirection()
                .hasRedirectedUrl("/public/index")
                .cookies()
                .containsKey("lang");
    }

    // ======================================================
// 🔵 TEST setLang() idioma inválido
// ======================================================
// ✅ Si idioma no válido usa "es" y redirige a /public/index
    @Test
    @DisplayName("GET /public/set-lang con idioma inválido usa es por defecto")
    void setLangInvalid() {

        var result = mockMvcTester.get()
                .uri("/public/set-lang?lang=fr") // 👈 Idioma inválido
                .exchange(); // 👈 Sin referer

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 302
                .hasRedirectedUrl("/public/index") // 👈 Redirección por defecto
                .cookies()
                .containsKey("lang"); // 👈 Cookie creada
    }
}