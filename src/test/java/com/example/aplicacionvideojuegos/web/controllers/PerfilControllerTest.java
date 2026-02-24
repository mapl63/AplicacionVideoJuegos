package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.users.models.User;
import com.example.aplicacionvideojuegos.rest.users.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// ======================================================
//IMPORTANTE PARA POST PUT DELETE
// ======================================================
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@SpringBootTest
@AutoConfigureMockMvc
class PerfilControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private UserService userService;

    User user = User.builder()
            .username("marius29")
            .nombre("Marius")
            .apellidos("Lopez")
            .build();


    // ======================================================
// 🔵 TEST showProfile
// ======================================================
// ✅ Devuelve perfil del usuario autenticado
    @WithMockUser(username = "marius29")
    @Test
    @DisplayName("GET /app/perfil devuelve perfil del usuario")
    void showProfile() {

        when(userService.findByUsername("marius29"))
                .thenReturn(Optional.of(user));

        var result = mockMvcTester.get()
                .uri("/app/perfil")
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("app/perfil")
                .model()
                .containsEntry("usuario", user);

        verify(userService, times(1))
                .findByUsername("marius29");
    }

    // ======================================================
// 🔵 TEST updateProfile SIN ERRORES
// ======================================================
// ✅ Actualiza perfil correctamente
    @WithMockUser(username = "marius29")
    @Test
    @DisplayName("POST /app/perfil/edit actualiza perfil correctamente")
    void updateProfileSinErrores() {

        User existingUser = user;

        when(userService.findByUsername("marius29"))
                .thenReturn(Optional.of(existingUser));

        var result = mockMvcTester.post()
                .uri("/app/perfil/edit")
                .with(csrf())
                .param("nombre", "Nuevo")
                .param("apellidos", "Apellido")
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("app/perfil")
                .model()
                .containsKeys("mensaje", "usuario");

        verify(userService, times(1))
                .findByUsername("marius29");

        verify(userService, times(1))
                .save(existingUser);
    }

    // ======================================================
// 🔵 TEST updateProfile CON ERRORES
// ======================================================
// ✅ Si hay errores no guarda y muestra mensaje
    @WithMockUser(username = "marius29")
    @Test
    @DisplayName("POST /app/perfil/edit con errores no actualiza perfil")
    void updateProfileConErrores() {

        var result = mockMvcTester.post()
                .uri("/app/perfil/edit")
                .with(csrf()) // 👈 IMPORTANTE para evitar 403
                .param("nombre", "") // 👈 Forzamos error si hay validación
                .param("apellidos", "")
                .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("app/perfil")
                .model()
                .containsEntry("mensaje",
                        "Ha ocurrido un error al actualizar el perfil.");

        verify(userService, never())
                .save(any(User.class)); // 👈 NO debe guardar
    }
}