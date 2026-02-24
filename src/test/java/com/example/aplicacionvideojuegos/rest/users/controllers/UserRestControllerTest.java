package com.example.aplicacionvideojuegos.rest.users.controllers;

import com.example.aplicacionvideojuegos.rest.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.rest.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.rest.users.dto.UserResponse;
import com.example.aplicacionvideojuegos.rest.users.exceptions.UserNotFound;
import com.example.aplicacionvideojuegos.rest.users.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


// Podemos usar el contexto de Spring para autenticar o usar un usuario mockeado:
// @WithMockUser(username = "pepe", roles = {"USER"})
// @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
// Porque está dado de alta en la base de datos data.sql
// @WithUserDetails(value = "admin")
// En el ejemplo siguiente, se va a ejecutar usando el usuario admin con roles de usuario y admin
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
@SpringBootTest
@AutoConfigureMockMvc()
class UserRestControllerTest {

    private final String ENDPOINT = "/api/v1/users";

    private final UserResponse userResponse = UserResponse.builder()
            .id(99L)
            .nombre("marius")
            .apellidos("puruguay")
            .username("marius1")
            .email("marius@test.com")
            .build();

    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(99L)
            .nombre("marius")
            .apellidos("puruguay")
            .username("marius1")
            .email("marius@test.com")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private UserService usersService;

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO NO AUTENTICADO
    // NO PUEDE ACCEDER AL ENDPOINT /api/v1/users
    // Y RECIBE UN 403 FORBIDDEN
    // ======================================================
    @Test
    @WithAnonymousUser // 👈 Ejecuta el test como si NO hubiera ningún usuario autenticado (usuario anónimo)
    void NotAuthenticated() {

        var result = mockMvcTester.get()   // 👈 Simula una petición HTTP GET
                .uri(ENDPOINT)      // 👈 La petición va al endpoint "/api/v1/users"
                .exchange();        // 👈 Ejecuta realmente la petición contra el contexto de Spring

        assertThat(result)       // 👈 Cogemos la respuesta que devuelve el servidor
                .hasStatus(HttpStatus.FORBIDDEN);  // 👈 Comprobamos que devuelve 403 (acceso prohibido)
    }

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
    // PUEDE OBTENER LA LISTA DE USUARIOS
    // Y RECIBE UN 200 OK CON LOS DATOS PAGINADOS
    // ======================================================
    @Test
    void findAll() {

        // 1️⃣ ARRANGE
        var userResponses = List.of(userResponse); // 👈 Creamos una lista simulada con 1 usuario
        Page<UserResponse> page = new PageImpl<>(userResponses); // 👈 Simulamos una página de resultados
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // 👈 Simulamos paginación por defecto

        when(usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable))
                .thenReturn(page); // 👈 Decimos que cuando el controller llame al service, devuelva esa página

        // 2️⃣ ACT
        var result = mockMvcTester.get()  // 👈 Simulamos petición GET
                .uri(ENDPOINT)                // 👈 A "/api/v1/users"
                .exchange();                  // 👈 Ejecutamos la petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatusOk()                // 👈 Comprobamos que devuelve 200 OK
                .bodyJson().satisfies(json -> {  // 👈 Analizamos el cuerpo JSON de la respuesta
                    assertThat(json)
                            .extractingPath("$.content.length()") // 👈 Comprobamos tamaño del array content
                            .isEqualTo(userResponses.size());

                    assertThat(json)
                            .extractingPath("$.content[0]")       // 👈 Cogemos el primer elemento del array
                            .convertTo(UserResponse.class)       // 👈 Lo convertimos a UserResponse
                            .isEqualTo(userResponse);            // 👈 Y comprobamos que coincide con el esperado
                });

        // 4️⃣ VERIFY
        verify(usersService, times(1))
                .findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        // 👈 Verificamos que el controller llamó al service exactamente una vez
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
    // PUEDE OBTENER UN USUARIO POR SU ID
    // Y RECIBE UN 200 OK CON LOS DATOS CORRECTOS
// ======================================================
    @Test
    void findById() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario preparado (99L)

        when(usersService.findById(anyLong())).thenReturn(userInfoResponse); // 👈 Simulamos que el service devuelve el usuario esperado

        var result = mockMvcTester.get() // 👈 Simulamos una petición HTTP GET
                .uri(ENDPOINT + "/" + id.toString()) // 👈 A la ruta "/api/v1/users/{id}"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos las comprobaciones
                .hasStatusOk() // 👈 Verificamos que devuelve 200 OK
                .bodyJson() // 👈 Accedemos al cuerpo JSON de la respuesta
                .convertTo(UserResponse.class) // 👈 Convertimos el JSON a objeto UserResponse
                .isEqualTo(userResponse); // 👈 Comprobamos que coincide con el esperado

        verify(usersService, only()).findById(anyLong()); // 👈 Verificamos que solo se llamó una vez al método findById del service
    }

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI EL USUARIO NO EXISTE
    // EL ENDPOINT DEVUELVE UN 404 NOT FOUND
    // Y LANZA LA EXCEPCIÓN CORRECTA
    // ======================================================
    @Test
    void findById_NotFound() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario que vamos a buscar

        when(usersService.findById(anyLong()))
                .thenThrow(new UserNotFound("No existe el usuario"));
        // 👈 Simulamos que el service lanza excepción porque no encuentra el usuario

        var result = mockMvcTester.get() // 👈 Simulamos una petición HTTP GET
                .uri(ENDPOINT + "/" + id.toString()) // 👈 A la ruta "/api/v1/users/{id}"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos las comprobaciones
                .hasStatus(HttpStatus.NOT_FOUND) // 👈 Verificamos que devuelve 404
                .hasFailed().failure() // 👈 Comprobamos que la petición ha fallado
                .isInstanceOf(UserNotFound.class) // 👈 Verificamos que la excepción es UserNotFound
                .hasMessageContaining("No existe el usuario"); // 👈 Comprobamos que el mensaje es el esperado

        verify(usersService, only()).findById(anyLong());
        // 👈 Verificamos que solo se llamó una vez al método findById del service
    }

    // ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
// PUEDE CREAR UN NUEVO USUARIO
// Y RECIBE UN 201 CREATED CON LOS DATOS CREADOS
// ======================================================
    @Test
    void createUser() {

        String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;  // 👈 Simulamos el cuerpo JSON que enviaría el cliente en la petición POST

        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse); // 👈 Simulamos que el service guarda el usuario y devuelve el objeto creado

        var result = mockMvcTester.post() // 👈 Simulamos una petición HTTP POST
                .uri(ENDPOINT) // 👈 A la ruta "/api/v1/users"
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody) // 👈 Enviamos el cuerpo de la petición
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos las comprobaciones
                .hasStatus(HttpStatus.CREATED) // 👈 Verificamos que devuelve 201 CREATED
                .bodyJson() // 👈 Accedemos al cuerpo JSON de la respuesta
                .convertTo(UserResponse.class) // 👈 Convertimos el JSON a objeto UserResponse
                .isEqualTo(userResponse); // 👈 Comprobamos que coincide con el usuario esperado

        verify(usersService, only()).save(any(UserRequest.class));  // 👈 Verificamos que solo se llamó una vez al método save del service
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI LA PASSWORD
    // TIENE MENOS DE 5 CARACTERES
    // EL ENDPOINT DEVUELVE 400 BAD REQUEST
    // Y NO LLAMA AL SERVICE
    // ======================================================
    @Test
    void createUserBadRequestPasswordMenosDe5Caracteres() {

        String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "1234"
           }
          """;
        // 👈 Simulamos un JSON con password inválida (menos de 5 caracteres)

        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);  // 👈 Aunque lo mockeemos, NO debería llamarse porque fallará la validación antes

        var result = mockMvcTester.post() // 👈 Simulamos petición HTTP POST
                .uri(ENDPOINT) // 👈 A "/api/v1/users"
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody) // 👈 Enviamos el cuerpo inválido
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatus(HttpStatus.BAD_REQUEST) // 👈 Debe devolver 400 porque falla validación
                .bodyJson() // 👈 Accedemos al cuerpo JSON de error
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("password"); // 👈 Verificamos que el error está asociado al campo password
                });

        verify(usersService, never()).save(any(UserRequest.class));  // 👈 Verificamos que el service NO fue llamado porque la validación falló antes
    }

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI NOMBRE, APELLIDOS Y EMAIL
    // ESTÁN VACÍOS
    // EL ENDPOINT DEVUELVE 400 BAD REQUEST
    // Y NO LLAMA AL SERVICE
    // ======================================================
    @Test
    void createUser_BadRequestNombreApellidosEmailTodoEnBlanco() {

        String requestBody = """
          {
           "nombre": "",
           "apellidos": "",
           "username": "test",
           "email": "",
           "password": "test1234"
           }
          """;
        // 👈 Simulamos un JSON con varios campos obligatorios vacíos

        when(usersService.save(any(UserRequest.class)))
                .thenReturn(userResponse);  // 👈 Aunque lo mockeemos, NO debería llamarse porque fallará la validación antes

        var result = mockMvcTester.post() // 👈 Simulamos petición HTTP POST
                .uri(ENDPOINT) // 👈 A "/api/v1/users"
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody) // 👈 Enviamos el cuerpo inválido
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatus(HttpStatus.BAD_REQUEST) // 👈 Debe devolver 400 por error de validación
                .bodyJson() // 👈 Accedemos al cuerpo JSON de error
                .hasPathSatisfying("$.errores", path -> {
                    assertThat(path).hasFieldOrProperty("nombre");      // 👈 Verificamos error en nombre
                    assertThat(path).hasFieldOrProperty("apellidos");   // 👈 Verificamos error en apellidos
                    assertThat(path).hasFieldOrProperty("email");       // 👈 Verificamos error en email
                });

        verify(usersService, never()).save(any(UserRequest.class));  // 👈 Verificamos que el service NO fue llamado porque la validación falló antes
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
    // PUEDE ACTUALIZAR UN USUARIO EXISTENTE
    // Y RECIBE UN 200 OK CON LOS DATOS ACTUALIZADOS
    // ======================================================
    @Test
    void updateUser() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario que vamos a actualizar

        String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;
        // 👈 Simulamos el JSON que enviaría el cliente para actualizar el usuario

        when(usersService.update(anyLong(), any(UserRequest.class)))
                .thenReturn(userResponse);  // 👈 Simulamos que el service actualiza correctamente y devuelve el usuario actualizado

        var result = mockMvcTester.put() // 👈 Simulamos una petición HTTP PUT
                .uri(ENDPOINT + "/" + id) // 👈 A la ruta "/api/v1/users/{id}"
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody) // 👈 Enviamos el cuerpo con los datos actualizados
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatusOk() // 👈 Verificamos que devuelve 200 OK
                .bodyJson() // 👈 Accedemos al cuerpo JSON
                .convertTo(UserResponse.class) // 👈 Convertimos el JSON a objeto UserResponse
                .isEqualTo(userResponse); // 👈 Comprobamos que coincide con el esperado

        verify(usersService, only()).update(anyLong(), any(UserRequest.class)); // 👈 Verificamos que solo se llamó una vez al método update del service
    }


    // ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI INTENTAMOS ACTUALIZAR
// UN USUARIO QUE NO EXISTE
// EL ENDPOINT DEVUELVE 404 NOT FOUND
// ======================================================
    @Test
    void updateUser_NotFound() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario que intentamos actualizar

        String requestBody = """
          {
           "nombre": "test",
           "apellidos": "test",
           "username": "test",
           "email": "test@test.com",
           "password": "test1234"
           }
          """;
        // 👈 Simulamos el JSON con los datos de actualización

        when(usersService.update(anyLong(), any(UserRequest.class)))
                .thenThrow(new UserNotFound("No existe el usuario"));
        // 👈 Simulamos que el service lanza excepción porque el usuario no existe

        var result = mockMvcTester.put() // 👈 Simulamos petición HTTP PUT
                .uri(ENDPOINT + "/" + id) // 👈 A "/api/v1/users/{id}"
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody) // 👈 Enviamos el cuerpo de la petición
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatus(HttpStatus.NOT_FOUND) // 👈 Verificamos que devuelve 404
                .hasFailed().failure() // 👈 Comprobamos que la petición ha fallado
                .isInstanceOf(UserNotFound.class) // 👈 Verificamos que la excepción es UserNotFound
                .hasMessageContaining("No existe el usuario"); // 👈 Comprobamos que el mensaje es el esperado

        verify(usersService, only()).update(anyLong(), any(UserRequest.class));
        // 👈 Verificamos que solo se llamó una vez al método update del service
    }

    // ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
// PUEDE ELIMINAR UN USUARIO EXISTENTE
// Y RECIBE UN 204 NO CONTENT
// ======================================================
    @Test
    void deleteUser() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario que vamos a eliminar

        doNothing().when(usersService).deleteById(anyLong());
        // 👈 Simulamos que el service elimina correctamente el usuario (no devuelve nada)

        var result = mockMvcTester.delete() // 👈 Simulamos una petición HTTP DELETE
                .uri(ENDPOINT + "/" + id) // 👈 A la ruta "/api/v1/users/{id}"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatus(HttpStatus.NO_CONTENT);
        // 👈 Verificamos que devuelve 204 NO CONTENT (eliminación correcta sin cuerpo)

        verify(usersService, times(1)).deleteById(anyLong());
        // 👈 Verificamos que el controller llamó exactamente una vez al método deleteById
    }



    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI INTENTAMOS ELIMINAR
    // UN USUARIO QUE NO EXISTE
    // EL ENDPOINT DEVUELVE 404 NOT FOUND
    // ======================================================
    @Test
    void deleteUser_NotFound() {

        Long id = userResponse.getId(); // 👈 Obtenemos el id del usuario que intentamos eliminar

        doThrow(new UserNotFound("No existe el usuario"))
                .when(usersService).deleteById(anyLong());
        // 👈 Simulamos que el service lanza excepción porque el usuario no existe

        var result = mockMvcTester.delete() // 👈 Simulamos petición HTTP DELETE
                .uri(ENDPOINT + "/" + id) // 👈 A "/api/v1/users/{id}"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatus(HttpStatus.NOT_FOUND) // 👈 Verificamos que devuelve 404
                .hasFailed().failure() // 👈 Comprobamos que la petición ha fallado
                .isInstanceOf(UserNotFound.class) // 👈 Verificamos que la excepción es UserNotFound
                .hasMessageContaining("No existe el usuario");
        // 👈 Comprobamos que el mensaje es el esperado

        verify(usersService, only()).deleteById(anyLong());
        // 👈 Verificamos que solo se llamó una vez al método deleteById del service
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO AUTENTICADO
    // PUEDE OBTENER SU PROPIO PERFIL (/me/profile)
    // Y RECIBE UN 200 OK CON SUS DATOS
    // ======================================================
    @Test
    @WithUserDetails("admin") // 👈 Usa un usuario REAL de la base de datos (data.sql)
    void me() {

        when(usersService.findById(anyLong()))
                .thenReturn(userInfoResponse);
        // 👈 Simulamos que el service devuelve la información del usuario autenticado

        var result = mockMvcTester.get() // 👈 Simulamos petición HTTP GET
                .uri(ENDPOINT + "/me/profile") // 👈 A "/api/v1/users/me/profile"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result) // 👈 Comenzamos comprobaciones
                .hasStatusOk() // 👈 Verificamos que devuelve 200 OK
                .bodyJson() // 👈 Accedemos al cuerpo JSON
                .convertTo(UserResponse.class) // 👈 Convertimos el JSON a objeto UserResponse
                .isEqualTo(userResponse); // 👈 Comprobamos que coincide con el esperado

        verify(usersService, only()).findById(anyLong());
        // 👈 Verificamos que solo se llamó una vez al método findById del service
    }

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE UN USUARIO NO AUTENTICADO
    // NO PUEDE ACCEDER A SU PERFIL (/me/profile)
    // Y RECIBE UN 403 FORBIDDEN
    // ======================================================
    @Test
    @WithAnonymousUser // 👈 Simulamos que el usuario NO está autenticado
    void me_AnonymousUser() {

        var result = mockMvcTester.get() // 👈 Simulamos una petición HTTP GET
                .uri(ENDPOINT + "/me/profile") // 👈 A la ruta "/api/v1/users/me/profile"
                .exchange(); // 👈 Ejecutamos la petición

        assertThat(result)
                .hasStatus(HttpStatus.FORBIDDEN);
        // 👈 Verificamos que devuelve 403 FORBIDDEN porque no hay autenticación
    }


}
