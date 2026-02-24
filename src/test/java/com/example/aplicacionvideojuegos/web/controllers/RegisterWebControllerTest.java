package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignUpRequest;
import com.example.aplicacionvideojuegos.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class RegisterWebControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AuthenticationService authenticationService;


    // ======================================================
// 🔵 TEST showRegisterForm
// ======================================================
// ✅ Devuelve vista de registro con modelo correcto
    @Test
    @DisplayName("GET /auth/register devuelve formulario de registro")
    void showRegisterForm() {

        var result = mockMvcTester.get()
                .uri("/auth/register")
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("auth/register")
                .model()
                .containsKeys("user");
    }

    // ======================================================
// 🔵 TEST processRegister SIN ERRORES
// ======================================================
// ✅ Registra usuario y redirige a login
    @Test
    @DisplayName("POST /auth/register registra usuario correctamente")
    void processRegisterSuccess() {

        var result = mockMvcTester.post()
                .uri("/auth/register")
                .with(csrf()) // 👈 IMPORTANTE
                .param("nombre","Marius")
                .param("apellidos","Lopez")
                .param("username", "marius29")
                .param("password", "12345")
                .param("passwordComprobacion", "12345")
                .param("email", "marius@email.com")
                .exchange();

        assertThat(result)
                .hasStatus3xxRedirection()
                .hasRedirectedUrl("/auth/login");

        verify(authenticationService, times(1))
                .signUp(any(UserSignUpRequest.class));
    }
}