package com.example.aplicacionvideojuegos.rest.auth.services.authentication;

import com.example.aplicacionvideojuegos.rest.auth.dto.JwtAuthResponse;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignInRequest;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignUpRequest;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthDifferentPasswords;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthExistingUsernameOrEmail;
import com.example.aplicacionvideojuegos.rest.auth.exceptions.AuthSignInNotValid;
import com.example.aplicacionvideojuegos.rest.auth.repositories.AuthUsersRepository;
import com.example.aplicacionvideojuegos.rest.auth.services.jwt.JwtService;
import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.repositories.ClienteRepository;
import com.example.aplicacionvideojuegos.rest.users.models.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthUsersRepository authUsersRepository;

    @Mock
    private ClienteRepository  clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE CUANDO LAS PASSWORDS COINCIDEN
    // EN EL SIGN UP
    // SE CREA EL USUARIO Y SE DEVUELVE UN TOKEN JWT
    // ======================================================
    @Test
    public void testSignUp_WhenPasswordsMatch_ShouldReturnToken() {

        UserSignUpRequest request = UserSignUpRequest.builder() // 👈 Creamos la petición de registro
                .nombre("Test")
                .apellidos("User")
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .passwordComprobacion("password") // 👈 Las passwords coinciden
                .build();

        User userStored = new User(); // 👈 Simulamos el usuario guardado en base de datos
        when(authUsersRepository.save(any(User.class)))
                .thenReturn(userStored); // 👈 Mockeamos el guardado en BD

        String token = "test_token"; // 👈 Simulamos el token generado
        when(jwtService.generateToken(userStored))
                .thenReturn(token); // 👈 Mockeamos la generación del JWT

        JwtAuthResponse response = authenticationService.signUp(request);   // 👈 Ejecutamos el método real que estamos probando

        assertAll("Sign Up", // 👈 Agrupamos todas las verificaciones
                () -> assertNotNull(response), // 👈 Comprobamos que la respuesta no es null
                () -> assertEquals(token, response.getToken()), // 👈 Comprobamos que devuelve el token esperado
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),  // 👈 Verificamos que el usuario se guardó una vez
                () -> verify(jwtService, times(1)).generateToken(userStored)    // 👈 Verificamos que se generó el token una vez
        );
    }

    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI LAS CONTRASEÑAS
    // NO COINCIDEN EN EL SIGN UP,
    // SE LANZA LA EXCEPCIÓN AuthDifferentPasswords
    // ======================================================
    @Test
    public void testSignUp_WhenPasswordsDoNotMatch_ShouldThrowException() {

        // 1️⃣ ARRANGE
        UserSignUpRequest request = UserSignUpRequest.builder()  // 👈 Creamos petición simulada
                .nombre("Test")
                .apellidos("User")
                .username("testuser")
                .email("test@example.com")
                .password("password1")                // 👈 Password 1
                .passwordComprobacion("password2")    // 👈 Password diferente
                .build();

        // 2️⃣ ACT + ASSERT
        assertThrows(AuthDifferentPasswords.class,  // 👈 Esperamos esta excepción
                () -> authenticationService.signUp(request));  // 👈 Ejecutamos el método

        // 👈 No hace falta verify porque el método debe fallar antes de tocar repositorios
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI EL USERNAME O EMAIL
    // YA EXISTEN EN BASE DE DATOS,
    // SE LANZA AuthExistingUsernameOrEmail
    // ======================================================
    @Test
    public void testSignUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {

        // 1️⃣ ARRANGE
        UserSignUpRequest request = UserSignUpRequest.builder()   // 👈 Creamos petición válida
                .nombre("Test")
                .apellidos("User")
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .passwordComprobacion("password")
                .build();

        when(passwordEncoder.encode(any()))                      // 👈 Simulamos que el password se encripta
                .thenReturn("encoded_password");

        when(clienteRepository.save(any()))                      // 👈 Simulamos guardado de cliente
                .thenReturn(new Cliente());

        when(authUsersRepository.save(any(User.class)))          // 👈 Simulamos error de duplicado
                .thenThrow(DataIntegrityViolationException.class);

        // 2️⃣ ACT + ASSERT
        assertThrows(AuthExistingUsernameOrEmail.class,          // 👈 Esperamos esta excepción
                () -> authenticationService.signUp(request));

        // 👈 No hace falta verify porque la excepción es lo importante aquí
    }


    // ======================================================
    // 🔵 ESTE MÉTODO PRUEBA QUE SI LAS CREDENCIALES SON
    // CORRECTAS EN EL SIGN IN,
    // SE DEVUELVE UN TOKEN JWT
    // ======================================================
    @Test
    public void testSignIn_WhenValidCredentials_ShouldReturnToken() {

        // 1️⃣ ARRANGE
        UserSignInRequest request = UserSignInRequest.builder()   // 👈 Creamos petición de login
                .username("testuser")
                .password("password")
                .build();

        User user = new User();                                   // 👈 Simulamos usuario existente

        when(authUsersRepository.findByUsername(request.getUsername()))  // 👈 Simulamos búsqueda en BD
                .thenReturn(Optional.of(user));

        String token = "test_token";                               // 👈 Token falso
        when(jwtService.generateToken(user))                       // 👈 Simulamos generación de JWT
                .thenReturn(token);

        // 2️⃣ ACT
        JwtAuthResponse response = authenticationService.signIn(request);  // 👈 Ejecutamos método

        // 3️⃣ ASSERT
        assertAll("Sign In",
                () -> assertNotNull(response),                     // 👈 La respuesta no debe ser null
                () -> assertEquals(token, response.getToken()),    // 👈 El token debe coincidir
                () -> verify(authenticationManager, times(1))      // 👈 Debe autenticarse
                        .authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(authUsersRepository, times(1))        // 👈 Debe buscar usuario
                        .findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1))                 // 👈 Debe generar JWT
                        .generateToken(user)
        );
    }

// ======================================================
// 🔵 ESTE MÉTODO PRUEBA QUE SI EL USUARIO NO EXISTE
// EN EL SIGN IN,
// SE LANZA AuthSignInNotValid
// ======================================================
    @Test
    public void testSignIn_WhenInvalidCredentials_ShouldThrowException() {

        // 1️⃣ ARRANGE
        UserSignInRequest request = UserSignInRequest.builder()   // 👈 Creamos petición de login
                .username("testuser")
                .password("password")
                .build();

        when(authUsersRepository.findByUsername(request.getUsername()))  // 👈 Simulamos que NO existe usuario
                .thenReturn(Optional.empty());

        // 2️⃣ ACT + ASSERT
        assertThrows(AuthSignInNotValid.class,      // 👈 Esperamos esta excepción
                () -> authenticationService.signIn(request));

        // 👈 No hace falta verify porque lo importante es que falle correctamente
    }
}
