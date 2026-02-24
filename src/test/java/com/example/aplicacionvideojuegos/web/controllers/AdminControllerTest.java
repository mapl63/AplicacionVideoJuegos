package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import com.example.aplicacionvideojuegos.web.services.I18nService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private VideoJuegoService videoJuegoService;

    @MockitoBean
    private I18nService i18nService;

    VideoJuegosResponseDto dto1 = VideoJuegosResponseDto.builder()
            .id(1L)
            .nombre("gta v")
            .precio(59.99)
            .fecha_lanzamiento(LocalDate.of(2012,5,14))
            .genero("Accion")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    VideoJuegosResponseDto dto2 = VideoJuegosResponseDto.builder()
            .id(2L)
            .nombre("fifa 15")
            .precio(89.99)
            .fecha_lanzamiento(LocalDate.of(2015,9,21))
            .genero("Deportes")
            .plataforma(VideoJuegos.Plataforma.PS4)
            .edad(3)
            .build();

    VideoJuegos juego1 = VideoJuegos.builder()
            .id(3L)
            .nombre("Crisom desert")
            .precio(99.99)
            .fecha_lanzamiento(LocalDate.of(2012,5,14))
            .genero("RPG")
            .plataforma(VideoJuegos.Plataforma.XBOXONE)
            .edad(18)
            .build();

    VideoJuegos juego2 = VideoJuegos.builder()
            .id(4L)
            .nombre("fc 26")
            .precio(129.99)
            .fecha_lanzamiento(LocalDate.of(2002,5,14))
            .genero("Terror")
            .plataforma(VideoJuegos.Plataforma.PC)
            .edad(13)
            .build();

// ========================================
//📌 CHULETILLA TEST MVC – SPRING BOOT
//========================================
    /*


🔹 1️⃣ GET que devuelve vista
----------------------------------------
assertThat(result)
    .hasStatusOk()              // 👈 200
    .hasViewName("ruta/vista"); // 👈 Vista correcta


🔹 2️⃣ GET que devuelve fragmento
----------------------------------------
assertThat(result)
    .hasStatusOk()
    .hasViewName("fragments/fragmento");


🔹 3️⃣ POST correcto (sin errores)
----------------------------------------
assertThat(result)
    .hasStatus3xxRedirection()          // 👈 302
    .hasRedirectedUrl("/ruta");         // 👈 Redirección correcta


🔹 4️⃣ POST con errores de validación
----------------------------------------
assertThat(result)
    .hasStatusOk()                      // 👈 200
    .hasViewName("ruta/form");          // 👈 Vuelve al formulario


🔹 5️⃣ Comprobar atributo en modelo
----------------------------------------
assertThat(result)
    .model()
    .containsKeys("atributo");

assertThat(result)
    .model()
    .containsEntry("atributo", objetoEsperado);


🔹 6️⃣ Comprobar que es una Page
----------------------------------------
.hasEntrySatisfying("page", value ->
    assertThat(value).isInstanceOf(Page.class));


🔹 7️⃣ Verificar llamada a servicio
----------------------------------------
verify(servicio, times(1)).metodo(any());

verify(servicio, only()).metodo(any());


🔹 8️⃣ Recordatorio CSRF (para POST, PUT, DELETE)
----------------------------------------
.with(csrf())  // 👈 Obligatorio si hay Spring Security


🔹 9️⃣ Recordatorio seguridad
----------------------------------------
@WithUserDetails("usuarioAdmin")
*/

    // ======================================================
    // 🔵 TEST LISTADO ADMIN
    // ======================================================
    // ✅ Comprueba que el admin puede ver la lista paginada
    @WithUserDetails("marius29") // 👈 Simula usuario autenticado con ROLE_ADMIN
    @Test
    @DisplayName("GET /admin/videojuegos - Devuelve listado paginado para ADMIN") // 👈 Nombre descriptivo del test
    void videoJuegosListadoAdmin() {

        List<VideoJuegosResponseDto> lista = List.of(dto1, dto2); // 👈 Lista simulada con 2 videojuegos DTO

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // 👈 Simulamos paginación (page 0, size 10, orden ascendente)

        Page<VideoJuegosResponseDto> page =
                new PageImpl<>(lista, pageable, lista.size()); // 👈 Creamos una página falsa con los datos simulados

        when(videoJuegoService.findAll(any(), any(), any(), any(Pageable.class)))
                .thenReturn(page); // 👈 Cuando el controller llame al service, devolverá esta página

        var result = mockMvcTester.get() // 👈 Simulamos petición HTTP GET
                .uri("/admin/videojuegos") // 👈 Endpoint que estamos probando
                .contentType(MediaType.TEXT_HTML) // 👈 Indicamos que esperamos una vista HTML
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result)
                .hasStatusOk() // 👈 Verificamos que devuelve 200 OK
                .hasViewName("admin/videojuegos/lista") // 👈 Comprobamos que devuelve la vista correcta
                .model()
                .containsKeys("page") // 👈 El modelo debe contener el atributo "page"
                .hasEntrySatisfying("page", value ->
                        assertThat(value).isInstanceOf(Page.class)); // 👈 Comprobamos que "page" es un objeto Page

        verify(videoJuegoService, times(1))
                .findAll(any(), any(), any(), any(Pageable.class)); // 👈 Verificamos que el service fue llamado exactamente una vez
    }

// ======================================================
// 🔵 TEST FILTRO ADMIN
// ======================================================
// ✅ Comprueba que el admin puede filtrar videojuegos por nombre
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/filter - Devuelve fragmento con lista filtrada")
    void videoJuegosFilter() {

        List<VideoJuegosResponseDto> lista = List.of(dto1); // 👈 Lista simulada con 1 resultado filtrado

        Pageable pageable = PageRequest.of(0, 4, Sort.by("id").ascending()); // 👈 Paginación del método filter (size 4)

        Page<VideoJuegosResponseDto> page =
                new PageImpl<>(lista, pageable, lista.size()); // 👈 Creamos página simulada

        when(videoJuegoService.findAll(
                any(), any(), any(), any(Pageable.class)))
                .thenReturn(page); // 👈 Simulamos respuesta del servicio

        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/admin/videojuegos/filter?nombre=gta")    // 👈 Añadimos parámetro de filtro
                .contentType(MediaType.TEXT_HTML) // 👈 Esperamos HTML (fragmento)
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 200 OK
                .hasViewName("fragments/listaJuegos") // 👈 Devuelve el fragmento correcto
                .model()
                .containsKeys("page") // 👈 Modelo contiene "page"
                .hasEntrySatisfying("page", value ->
                        assertThat(value).isInstanceOf(Page.class)); // 👈 Es una Page

        verify(videoJuegoService, times(1))
                .findAll(any(), any(), any(), any(Pageable.class)); // 👈 Service llamado una vez
    }

// ======================================================
// 🔵 TEST DETALLE ADMIN
// ======================================================
// ✅ Comprueba que el admin puede ver el detalle de un videojuego
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/{id} - Devuelve detalle del videojuego")
    void getByIdAdmin() {

        Long id = 1L; // 👈 ID que vamos a consultar

        when(videoJuegoService.buscarPorId(id))
                .thenReturn(Optional.of(juego1)); // 👈 Simulamos que el service encuentra el juego

        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/admin/videojuegos/{id}", id) // 👈 Endpoint con path variable
                .contentType(MediaType.TEXT_HTML) // 👈 Esperamos vista HTML
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 Verificamos 200 OK
                .hasViewName("admin/videojuegos/detalle") // 👈 Vista correcta
                .model()
                .containsKeys("videojuego") // 👈 Modelo contiene atributo "videojuego"
                .containsEntry("videojuego", juego1); // 👈 Y es el objeto esperado

        verify(videoJuegoService, times(1))
                .buscarPorId(id); // 👈 Verificamos que el service fue llamado una vez
    }

// ======================================================
// 🔵 TEST NUEVO VIDEOJUEGO FORM
// ======================================================
// ✅ Comprueba que el admin puede acceder al formulario de creación
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/new - Devuelve formulario de creación")
    void nuevoVideoJuegoForm() {

        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/admin/videojuegos/new") // 👈 Endpoint del formulario
                .contentType(MediaType.TEXT_HTML) // 👈 Esperamos vista HTML
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 Verificamos 200 OK
                .hasViewName("admin/videojuegos/form") // 👈 Vista correcta
                .model()
                .containsKeys("videojuegos", "modoEditar", "plataformas") // 👈 Modelo contiene estos atributos
                .containsEntry("modoEditar", false); // 👈 Debe estar en modo creación (false)
    }

// ======================================================
// 🔵 TEST NUEVO VIDEOJUEGO SUBMIT (SIN ERRORES)
// ======================================================
// ✅ Comprueba que se crea el videojuego y redirige correctamente
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/new - Guarda videojuego y redirige")
    void nuevoVideoJuegoSubmit() {

        when(i18nService.getMessage("videojuego.creado.ok"))
                .thenReturn("Videojuego creado correctamente"); // 👈 Simulamos mensaje i18n

        var result = mockMvcTester.post() // 👈 Simulamos petición POST
                .uri("/admin/videojuegos/new") // 👈 Endpoint del submit
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED) // 👈 Enviamos datos tipo formulario
                .param("nombre", "Nuevo Juego")
                .param("cliente","1")
                .param("precio", "59.99")
                .param("fecha_lanzamiento", "2012-05-14")
                .param("genero", "Accion")
                .param("plataforma", "PS5")
                .param("edad", "18") // 👈 Parámetros del formulario
                .exchange(); // 👈 Ejecutamos petición


        assertThat(result)
                .hasStatus3xxRedirection() // 👈 Debe redirigir (302)
                .hasRedirectedUrl("/admin/videojuegos"); // 👈 Redirección correcta

        verify(videoJuegoService, times(1))
                .save(any()); // 👈 Verificamos que se llamó al save

        verify(i18nService, times(1))
                .getMessage("videojuego.creado.ok"); // 👈 Verificamos mensaje i18n
    }


    // ======================================================
// 🔵 TEST NUEVO VIDEOJUEGO SUBMIT (CON ERRORES)
// ======================================================
// ✅ Comprueba que si hay errores de validación vuelve al formulario
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/new - Datos erróneos devuelve formulario")
    void nuevoVideoJuegoSubmitDatosErroneos() {

        var result = mockMvcTester.post() // 👈 Simulamos petición POST
                .uri("/admin/videojuegos/new") // 👈 Endpoint del submit
                .with(csrf()) // 👈 Necesario para POST
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "") // 👈 ERROR: vacío (si tiene @NotBlank)
                .param("cliente", "1")
                .param("precio", "") // 👈 ERROR: vacío (si tiene @NotNull)
                .param("fecha_lanzamiento", "2012-05-14")
                .param("genero", "Accion")
                .param("plataforma", "PS5")
                .param("edad", "18")
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200 (no redirige)
                .hasViewName("admin/videojuegos/form"); // 👈 Vuelve al formulario

        verify(videoJuegoService, never())
                .save(any()); // 👈 NO debe llamarse al save

        verifyNoInteractions(i18nService); // 👈 No se genera mensaje de éxito
    }

    // ======================================================
// 🔵 TEST EDITAR VIDEOJUEGO FORM (EXISTE)
// ======================================================
// ✅ Comprueba que carga el formulario en modo edición
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/{id}/edit - Devuelve formulario en modo edición")
    void editarVideoJuegoForm() {

        Long id = 3L; // 👈 ID que vamos a editar

        when(videoJuegoService.buscarPorId(id))
                .thenReturn(Optional.of(juego1)); // 👈 Simulamos que el juego existe

        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/admin/videojuegos/{id}/edit", id) // 👈 Endpoint editar
                .contentType(MediaType.TEXT_HTML)
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200
                .hasViewName("admin/videojuegos/form") // 👈 Carga el mismo form que crear
                .model()
                .containsKeys("videojuego", "videoJuegoId", "modoEditar", "plataformas") // 👈 Atributos necesarios
                .containsEntry("videoJuegoId", id) // 👈 ID correcto
                .containsEntry("modoEditar", true); // 👈 Está en modo edición

        verify(videoJuegoService, times(1))
                .buscarPorId(id); // 👈 Se llamó al service
    }


    // ======================================================
// 🔵 TEST EDITAR VIDEOJUEGO FORM (NO EXISTE)
// ======================================================
// ✅ Comprueba que si el videojuego no existe redirige al listado
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/{id}/edit - Redirige si no existe")
    void editarVideoJuegoNoExisteForm() {

        Long id = 99L; // 👈 ID que no existe

        when(videoJuegoService.buscarPorId(id))
                .thenReturn(Optional.empty()); // 👈 Simulamos que no se encuentra el juego

        var result = mockMvcTester.get() // 👈 Simulamos petición GET
                .uri("/admin/videojuegos/{id}/edit", id) // 👈 Endpoint editar
                .contentType(MediaType.TEXT_HTML)
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 Devuelve 302
                .hasRedirectedUrl("/admin/videojuegos"); // 👈 Redirige al listado

        verify(videoJuegoService, times(1))
                .buscarPorId(id); // 👈 Se llamó al service
    }


    // ======================================================
// 🔵 TEST EDITAR VIDEOJUEGO SUBMIT (SIN ERRORES)
// ======================================================
// ✅ Comprueba que actualiza el videojuego y redirige al detalle
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/{id}/edit - Actualiza y redirige")
    void editarVideoJuegoSubmit() {

        Long id = 3L; // 👈 ID que vamos a actualizar

        when(i18nService.getMessage("videojuego.actualizado.ok"))
                .thenReturn("Videojuego actualizado correctamente"); // 👈 Simulamos mensaje i18n

        var result = mockMvcTester.post() // 👈 Simulamos petición POST
                .uri("/admin/videojuegos/{id}/edit", id) // 👈 Endpoint editar submit
                .with(csrf()) // 👈 Necesario para POST
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "Juego Editado")
                .param("precio", "79.99")
                .param("fecha_lanzamiento", "2012-05-14")
                .param("genero", "Accion")
                .param("plataforma", "PS5")
                .param("edad", "18") // 👈 Parámetros válidos
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 Devuelve 302
                .hasRedirectedUrl("/admin/videojuegos/" + id); // 👈 Redirige al detalle

        verify(videoJuegoService, times(1))
                .update(eq(id), any()); // 👈 Se llamó al update con el id correcto

        verify(i18nService, times(1))
                .getMessage("videojuego.actualizado.ok"); // 👈 Se generó mensaje
    }



    // ======================================================
// 🔵 TEST EDITAR VIDEOJUEGO SUBMIT (CON ERRORES)
// ======================================================
// ✅ Comprueba que si hay errores vuelve al formulario y no actualiza
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/{id}/edit - Datos erróneos devuelve formulario")
    void editarVideoJuegoSubmitDatosErroneos() {

        Long id = 3L; // 👈 ID que intentamos editar

        var result = mockMvcTester.post() // 👈 Simulamos petición POST
                .uri("/admin/videojuegos/{id}/edit", id) // 👈 Endpoint editar submit
                .with(csrf()) // 👈 Necesario para POST
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "") // 👈 ERROR: vacío si tiene @NotBlank
                .param("precio", "") // 👈 ERROR: vacío si tiene @NotNull
                .param("fecha_lanzamiento", "2012-05-14")
                .param("genero", "Accion")
                .param("plataforma", "PS5")
                .param("edad", "18")
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatusOk() // 👈 Devuelve 200 (no redirige)
                .hasViewName("admin/videojuegos/form"); // 👈 Vuelve al formulario

        verify(videoJuegoService, never())
                .update(anyLong(), any()); // 👈 NO debe llamarse al update

        verify(i18nService, times(1))
                .getMessage("videojuego.actualizar.error"); // 👈 se genera mensaje de éxito
    }

    // ======================================================
// 🔵 TEST BORRAR VIDEOJUEGO (TOKEN CORRECTO)
// ======================================================
// ✅ Comprueba que si el token es válido se elimina y redirige
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/{id}/delete - Elimina si token válido")
    void borrarVideoJuegoTokenCorrecto() {

        Long id = 3L; // 👈 ID a eliminar
        String token = "token123"; // 👈 Token simulado

        when(i18nService.getMessage("videojuego.eliminado.ok"))
                .thenReturn("Videojuego eliminado"); // 👈 Simulamos mensaje éxito

        var result = mockMvcTester.post() // 👈 Simulamos POST
                .uri("/admin/videojuegos/{id}/delete", id) // 👈 Endpoint borrar
                .with(csrf()) // 👈 Necesario para POST
                .sessionAttr("deleteToken_" + id, token) // 👈 Simulamos token en sesión
                .param("deleteToken", token) // 👈 Enviamos el mismo token
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 Debe redirigir
                .hasRedirectedUrl("/admin/videojuegos"); // 👈 Redirige al listado

        verify(videoJuegoService, times(1))
                .deleteById(id); // 👈 Se llamó al delete

        verify(i18nService, times(1))
                .getMessage("videojuego.eliminado.ok"); // 👈 Se generó mensaje éxito
    }

// ======================================================
// 🔴 TEST BORRAR VIDEOJUEGO (TOKEN INCORRECTO)
// ======================================================
// ✅ Comprueba que si el token es incorrecto NO elimina
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("POST /admin/videojuegos/{id}/delete - No elimina si token incorrecto")
    void borrarVideoJuegoTokenIncorrecto() {

        Long id = 3L; // 👈 ID a eliminar
        String tokenSesion = "tokenCorrecto"; // 👈 Token guardado en sesión
        String tokenEnviado = "tokenIncorrecto"; // 👈 Token enviado distinto

        when(i18nService.getMessage("videojuego.delete.token.error"))
                .thenReturn("Token inválido"); // 👈 Simulamos mensaje error

        var result = mockMvcTester.post() // 👈 Simulamos POST
                .uri("/admin/videojuegos/{id}/delete", id) // 👈 Endpoint borrar
                .with(csrf()) // 👈 Necesario para POST
                .sessionAttr("deleteToken_" + id, tokenSesion) // 👈 Token en sesión
                .param("deleteToken", tokenEnviado) // 👈 Token distinto
                .exchange(); // 👈 Ejecutamos petición

        assertThat(result)
                .hasStatus3xxRedirection() // 👈 Redirige
                .hasRedirectedUrl("/admin/videojuegos/"); // 👈 OJO: aquí lleva barra final

        verify(videoJuegoService, never())
                .deleteById(anyLong()); // 👈 NO debe eliminar

        verify(i18nService, times(1))
                .getMessage("videojuego.delete.token.error"); // 👈 Genera mensaje error
    }


    // ======================================================
// 🔵 TEST SHOW MODAL BORRAR (EXISTE)
// ======================================================
// ✅ Comprueba que genera modal con token y datos correctos
    @WithUserDetails("marius29") // 👈 Simula usuario ADMIN autenticado
    @Test
    @DisplayName("GET /admin/videojuegos/{id}/delete/confirm - Devuelve modal")
    void showModalBorrar() {

        Long id = 3L; // 👈 ID del videojuego

        when(videoJuegoService.buscarPorId(id))
                .thenReturn(Optional.of(juego1)); // 👈 Simulamos que existe

        when(i18nService.getMessage(eq("videojuegos.borrar.mensaje"), any()))
                .thenReturn("¿Seguro que quieres borrar?"); // 👈 Mensaje dinámico

        when(i18nService.getMessage("videojuegos.borrar.titulo"))
                .thenReturn("Confirmar borrado"); // 👈 Título del modal

        var result = mockMvcTester.get() // 👈 Simulamos GET
                .uri("/admin/videojuegos/{id}/delete/confirm", id) // 👈 Endpoint
                .contentType(MediaType.TEXT_HTML)
                .exchange(); // 👈 Ejecutamos

        assertThat(result)
                .hasStatusOk() // 👈 200 OK
                .hasViewName("fragments/deleteModal") // 👈 Devuelve fragmento modal
                .model()
                .containsKeys("deleteUrl", "deleteToken", "deleteTitle", "deleteMessage"); // 👈 Modelo correcto

        verify(videoJuegoService, times(1))
                .buscarPorId(id); // 👈 Se llamó al service
    }
}