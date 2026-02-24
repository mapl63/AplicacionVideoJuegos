package com.example.aplicacionvideojuegos.rest.auth.controllers;

import com.example.aplicacionvideojuegos.rest.auth.dto.JwtAuthResponse;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignInRequest;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignUpRequest;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthDifferentPasswords;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthExistingUsernameOrEmail;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthSignInNotValid;
import com.example.aplicacionvideojuegos.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationRestControllerTest {

    private final String ENDPOINT = "/api/v1/auth";

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private AuthenticationService authenticationService;

// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE EL ENDPOINT /signup
// FUNCIONA CORRECTAMENTE Y DEVUELVE UN JWT
// CUANDO EL REGISTRO ES VÁLIDO
// ======================================================
// ✅ Devuelve 200 OK y un token JWT cuando el registro es correcto
    @Test
    void signUp() {
        // 👈 JSON que simulamos enviar al endpoint
        String requestBody = """                      
          {
           "nombre": "Test",
           "apellidos": "Test",
           "username": "test2",
           "email": "test@test.com",
           "password": "12345",
           "passwordComprobacion": "12345"
           }
          """;

        var jwtAuthResponse = JwtAuthResponse.builder()  // 👈 Simulamos respuesta del servicio
                .token("token")
                .build();

        // 1️⃣ ARRANGE
        when(authenticationService.signUp(any(UserSignUpRequest.class))) // 👈 Cuando el controller llame al service
                .thenReturn(jwtAuthResponse);                             // 👈 Devolvemos un token simulado

        // 2️⃣ ACT
        var result = mockMvcTester.post()        // 👈 Simulamos petición POST
                .uri(ENDPOINT + "/signup")       // 👈 A /api/v1/auth/signup
                .contentType(MediaType.APPLICATION_JSON) // 👈 Indicamos que enviamos JSON
                .content(requestBody)            // 👈 Enviamos el body
                .exchange();                     // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatusOk()                   // 👈 Esperamos 200 OK
                .bodyJson()
                .convertTo(JwtAuthResponse.class) // 👈 Convertimos respuesta JSON a objeto
                .isEqualTo(jwtAuthResponse);      // 👈 Comprobamos que coincide con lo esperado

        // 4️⃣ VERIFY
        verify(authenticationService, times(1))   // 👈 Verificamos que el controller llamó al service
                .signUp(any(UserSignUpRequest.class));
    }

// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI EL SERVICIO LANZA
// AuthDifferentPasswords,
// EL CONTROLLER DEVUELVE 400 BAD REQUEST
// ======================================================
// ❌ Devuelve 400 BAD REQUEST si las contraseñas no coinciden
    @Test
    void signUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
// 👈 JSON con contraseñas diferentes
        String requestBody = """                      
          {
           "nombre": "Test",
           "apellidos": "Test",
           "username": "test2",
           "email": "test@test.com",
           "password": "12345",
           "passwordComprobacion": "54321"
           }
          """;

        // 1️⃣ ARRANGE
        when(authenticationService.signUp(any(UserSignUpRequest.class))) // 👈 Simulamos que el service lanza excepción
                .thenThrow(new AuthDifferentPasswords("Las contraseñas no coinciden"));

        // 2️⃣ ACT
        var result = mockMvcTester.post()        // 👈 Simulamos petición POST
                .uri(ENDPOINT + "/signup")       // 👈 A /api/v1/auth/signup
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();                     // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST) // 👈 Esperamos 400
                .hasFailed().failure()
                .isInstanceOf(AuthDifferentPasswords.class) // 👈 Comprobamos tipo excepción
                .hasMessageContaining("no coinciden");      // 👈 Comprobamos mensaje

        // 4️⃣ VERIFY
        verify(authenticationService, times(1)) // 👈 Verificamos que se llamó al service
                .signUp(any(UserSignUpRequest.class));
    }

// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI EL USERNAME O EMAIL
// YA EXISTEN EN EL REGISTRO,
// EL CONTROLLER DEVUELVE 400 BAD REQUEST
// ======================================================
// ❌ Devuelve 400 BAD REQUEST si el usuario o email ya existen
    @Test
    void signUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {

        String requestBody = """                      
      {
       "nombre": "Test",
       "apellidos": "Test",
       "username": "test",
       "email": "test@test.com",
       "password": "12345",
       "passwordComprobacion": "12345"
       }
      """; // 👈 JSON válido pero simulamos duplicado

        // 1️⃣ ARRANGE
        when(authenticationService.signUp(any(UserSignUpRequest.class)))
                .thenThrow(new AuthExistingUsernameOrEmail(
                        "El usuario con username XXX o email XXX ya existe"));
        // 👈 Simulamos excepción de usuario duplicado

        // 2️⃣ ACT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();  // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)  // 👈 Esperamos 400
                .hasFailed().failure()
                .isInstanceOf(AuthExistingUsernameOrEmail.class) // 👈 Tipo excepción correcto
                .hasMessageContaining("ya existe");              // 👈 Mensaje correcto

        // 4️⃣ VERIFY
        verify(authenticationService, times(1))
                .signUp(any(UserSignUpRequest.class));  // 👈 El controller llamó al servicio
    }


// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI LOS CAMPOS OBLIGATORIOS
// ESTÁN VACÍOS EN EL SIGNUP,
// EL CONTROLLER DEVUELVE 400 CON ERRORES DE VALIDACIÓN
// ======================================================
// ❌ Devuelve 400 BAD REQUEST y errores por campo vacío
    @Test
    void signUp_BadRequest_When_Nombre_Apellidos_Email_Username_Empty_ShouldThrowException() {

        String requestBody = """                      
      {
       "nombre": "",
       "apellidos": "",
       "username": "",
       "email": "",
       "password": "12345",
       "passwordComprobacion": "12345"
       }
      """; // 👈 Campos obligatorios vacíos

        // 1️⃣ ACT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange(); // 👈 Ejecutamos petición

        // 2️⃣ ASSERT
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)  // 👈 Esperamos 400 por validación
                .bodyJson()
                .hasPathSatisfying("$.errors", path -> {
                    assertThat(path).hasFieldOrProperty("nombre");     // 👈 Error en nombre
                    assertThat(path).hasFieldOrProperty("apellidos");  // 👈 Error en apellidos
                    assertThat(path).hasFieldOrProperty("username");   // 👈 Error en username
                    assertThat(path).hasFieldOrProperty("email");      // 👈 Error en email
                });

        // 3️⃣ VERIFY
        verify(authenticationService, never())
                .signUp(any(UserSignUpRequest.class)); // 👈 El service NO debe ejecutarse
    }

// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE EL ENDPOINT /signin
// FUNCIONA CORRECTAMENTE Y DEVUELVE UN JWT
// CUANDO LAS CREDENCIALES SON VÁLIDAS
// ======================================================
// ✅ Devuelve 200 OK y un token JWT si el login es correcto
    @Test
    void signIn() {

        String requestBody = """                      
      {
       "username": "test2",
       "password": "12345"
       }
      """; // 👈 JSON válido de login

        var jwtAuthResponse = JwtAuthResponse.builder()  // 👈 Simulamos respuesta del servicio
                .token("token")
                .build();

        // 1️⃣ ARRANGE
        when(authenticationService.signIn(any(UserSignInRequest.class)))
                .thenReturn(jwtAuthResponse); // 👈 El service devuelve un token

        // 2️⃣ ACT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")   // 👈 /api/v1/auth/signin
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();                 // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatusOk()               // 👈 Esperamos 200 OK
                .bodyJson()
                .convertTo(JwtAuthResponse.class)
                .isEqualTo(jwtAuthResponse); // 👈 El token coincide

        // 4️⃣ VERIFY
        verify(authenticationService, times(1))
                .signIn(any(UserSignInRequest.class)); // 👈 Se llamó al service
    }


// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI EL SERVICIO LANZA
// AuthSignInNotValid,
// EL CONTROLLER DEVUELVE ERROR 4XX
// ======================================================
// ❌ Devuelve 4xx si las credenciales son incorrectas
    @Test
    void signIn_NotValid() {

        String requestBody = """                      
      {
       "username": "test2",
       "password": "password"
       }
      """; // 👈 JSON con credenciales incorrectas

        // 1️⃣ ARRANGE
        when(authenticationService.signIn(any(UserSignInRequest.class)))
                .thenThrow(new AuthSignInNotValid("Usuario o contraseña incorrectos"));
        // 👈 Simulamos error de login

        // 2️⃣ ACT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange(); // 👈 Ejecutamos petición

        // 3️⃣ ASSERT
        assertThat(result)
                .hasStatus4xxClientError()  // 👈 Esperamos error cliente (401 o 400 según tu handler)
                .hasFailed().failure()
                .isInstanceOf(AuthSignInNotValid.class)
                .hasMessageContaining("incorrectos");

        // 4️⃣ VERIFY
        verify(authenticationService, times(1))
                .signIn(any(UserSignInRequest.class)); // 👈 Se llamó al service
    }


// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI USERNAME Y PASSWORD
// ESTÁN VACÍOS EN EL SIGNIN,
// EL CONTROLLER DEVUELVE 400 CON ERRORES DE VALIDACIÓN
// ======================================================
// ❌ Devuelve 400 BAD REQUEST si username y password están vacíos
    @Test
    void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() {

        String requestBody = """                      
      {
       "username": "",
       "password": ""
       }
      """; // 👈 Campos obligatorios vacíos

        // 1️⃣ ACT
        var result = mockMvcTester.post()
                .uri(ENDPOINT + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange(); // 👈 Ejecutamos petición

        // 2️⃣ ASSERT
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)  // 👈 Esperamos 400 por validación
                .bodyJson()
                .hasPathSatisfying("$.errors", path -> {  // 👈 Spring devuelve "errors"
                    assertThat(path).hasFieldOrProperty("username");
                    assertThat(path).hasFieldOrProperty("password");
                });

        // 3️⃣ VERIFY
        verify(authenticationService, never())
                .signIn(any(UserSignInRequest.class)); // 👈 El service NO debe ejecutarse
    }
}
