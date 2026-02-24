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
import com.example.aplicacionvideojuegos.rest.users.models.Role;
import com.example.aplicacionvideojuegos.rest.users.models.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthUsersRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ClienteRepository clienteRepository;

    @Override
    public JwtAuthResponse signUp(UserSignUpRequest request) {

        log.info("Creando usuario: {}", request);

        // 1️⃣ Validar contraseñas
        if (!request.getPassword().contentEquals(request.getPasswordComprobacion())) {
            throw new AuthDifferentPasswords("Las contraseñas no coinciden");
        }

        try {

            // 2️⃣ Encriptar password
            String passwordHash = passwordEncoder.encode(request.getPassword());

            // 🔎 LOGS SOLO PARA DESARROLLO / CLASE
            log.info("====================================");
            log.info("PASSWORD ORIGINAL: {}", request.getPassword());
            log.info("PASSWORD BCRYPT : {}", passwordHash);
            log.info("====================================");

            // 3️⃣ Crear CLIENTE
            Cliente cliente = Cliente.builder()
                    .nombre(request.getNombre())
                    .build();

            clienteRepository.save(cliente);

            // 4️⃣ Crear USER asociado al cliente
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordHash)
                    .email(request.getEmail())
                    .nombre(request.getNombre())
                    .apellidos(request.getApellidos())
                    .cliente(cliente)
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();

            log.info("Creando usuario en BD: {}", user);

            // 5️⃣ Guardar usuario
            var userStored = authUsersRepository.save(user);

            // 6️⃣ Generar JWT (login automático backend)
            return JwtAuthResponse.builder()
                    .token(jwtService.generateToken(userStored))
                    .build();

        } catch (DataIntegrityViolationException ex) {
            throw new AuthExistingUsernameOrEmail(
                    "El usuario con username " + request.getUsername() +
                            " o email " + request.getEmail() + " ya existe"
            );
        }
    }


    @Override
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request);
        // Autenticamos y devolvemos el token
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = authUsersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthSignInNotValid("Usuario o contraseña incorrectos"));
        var jwt = jwtService.generateToken(user);
        return JwtAuthResponse.builder().token(jwt).build();
    }
}
