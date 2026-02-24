package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.users.services.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private UserService usuarioServicio;

    // ======================================================
// 🔵 TEST WELCOME
// ======================================================
// ✅ Comprueba que "/" redirige a /public/
    @Test
    @DisplayName("GET / redirige a /public/")
    void welcome() {

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/") // 👈 Endpoint raíz
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 302
                .hasRedirectedUrl("/public/"); // 👈 URL correcta
    }

    // ======================================================
// 🔵 TEST LOGIN
// ======================================================
// ✅ Comprueba que carga la vista login con modelo correcto
    @Test
    @DisplayName("GET /auth/login devuelve vista login")
    void login() {

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/auth/login") // 👈 Endpoint login
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatusOk() // 👈 200 OK
                .hasViewName("auth/login") // 👈 Vista correcta
                .model()
                .containsKeys("usuario"); // 👈 Modelo contiene atributo usuario
    }
}