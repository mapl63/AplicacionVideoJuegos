package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.web.services.I18nService;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomErrorControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private I18nService i18nService;

    // ======================================================
// 🔵 TEST ERROR POR DEFECTO (SIN STATUS)
// ======================================================
// ✅ Si no hay código devuelve error 500 por defecto
    @Test
    @DisplayName("GET /error - Devuelve error general por defecto")
    void errorGeneralPorDefecto() {

        when(i18nService.getMessage("error.general"))
                .thenReturn("Error general");

        var result = mockMvcTester.get()
                .uri("/error")
                .exchange();

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200 (vista error personalizada)
                .hasViewName("error")
                .model()
                .containsEntry("errorCode", "500")
                .containsEntry("errorTitle", "Error");
    }


// ======================================================
// 🔵 TEST ERROR 404
// ======================================================
// ✅ Comprueba que cuando el código es 404 muestra error personalizado
    @Test
    @DisplayName("GET /error - Devuelve error 404")
    void error404() {

        when(i18nService.getMessage("error.general"))
                .thenReturn("Error general"); // 👈 Mensaje por defecto

        when(i18nService.getMessage("error.404"))
                .thenReturn("Página no encontrada"); // 👈 Mensaje específico 404

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 404) // 👈 Simulamos código 404
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200 (vista personalizada)
                .hasViewName("error") // 👈 Vista correcta
                .model()
                .containsEntry("errorCode", "404") // 👈 Código correcto
                .containsEntry("errorTitle", "Página no encontrada"); // 👈 Título correcto

        verify(i18nService, times(1))
                .getMessage("error.404"); // 👈 Se llamó al mensaje 404
    }

    // ======================================================
// 🔵 TEST ERROR 403
// ======================================================
// ✅ Comprueba que cuando el código es 403 muestra mensaje de acceso prohibido
    @Test
    @DisplayName("GET /error - Devuelve error 403")
    void error403() {

        when(i18nService.getMessage("error.general"))
                .thenReturn("Error general"); // 👈 Mensaje por defecto

        when(i18nService.getMessage("error.403"))
                .thenReturn("Acceso prohibido"); // 👈 Mensaje específico 403

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 403) // 👈 Simulamos 403
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200 (vista personalizada)
                .hasViewName("error") // 👈 Vista correcta
                .model()
                .containsEntry("errorCode", "403") // 👈 Código correcto
                .containsEntry("errorTitle", "Acceso prohibido"); // 👈 Título correcto

        verify(i18nService, times(1))
                .getMessage("error.403"); // 👈 Se llamó al mensaje 403
    }


    // ======================================================
// 🔵 TEST ERROR 500
// ======================================================
// ✅ Comprueba que cuando el código es 500 muestra error interno
    @Test
    @DisplayName("GET /error - Devuelve error 500")
    void error500() {

        when(i18nService.getMessage("error.general"))
                .thenReturn("Error general"); // 👈 Mensaje por defecto

        when(i18nService.getMessage("error.500"))
                .thenReturn("Error interno del servidor"); // 👈 Mensaje específico 500

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/error")
                .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 500) // 👈 Simulamos 500
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200
                .hasViewName("error") // 👈 Vista correcta
                .model()
                .containsEntry("errorCode", "500") // 👈 Código correcto
                .containsEntry("errorTitle", "Error interno del servidor"); // 👈 Título correcto

        verify(i18nService, times(1))
                .getMessage("error.500"); // 👈 Se llamó al mensaje 500
    }
}