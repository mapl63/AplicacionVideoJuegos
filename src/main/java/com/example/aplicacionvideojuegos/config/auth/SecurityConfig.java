package com.example.aplicacionvideojuegos.config.auth;
import com.example.aplicacionvideojuegos.config.auth.LoginSuccessHandler;
import com.auth0.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*CORS*/
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginSuccessHandler loginSuccessHandler;

    @Value("${api.version}")
    private String apiVersion;

    /*
    *
    *tienes 4 bloques así dentro de SecurityConfig:

Order(1) → API

Order(2) → Swagger

Order(3) → H2

Order(4) → Web con login*/

    //    ========================================================
//    SECURITY FILTER CHAIN - API (REST +JWT)
//    ========================================================
    /*


    Este filtro protege la parte REST de la aplicación:

    Rutas afectadas:
    - /api/**
    - /ws/**
    - /graphql
    - /graphiql
    - /error/**

    Características principales:

    1) CSRF desactivado
       → Porque la API usa JWT y es stateless (no usa sesión).

    2) SessionCreationPolicy.STATELESS
       → El servidor no guarda sesiones.
       → Cada petición debe traer su token JWT.

    3) JWTAuthenticationFilter
       → Lee el header Authorization.
       → Valida el token.
       → Carga el usuario en el SecurityContext.

    4) Los roles NO se controlan aquí.
       → Se controlan con @PreAuthorize en los controllers.

    Este chain se ejecuta primero (@Order(1)).
    ========================================================
    */
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] apiPaths = {"/api/**", "/error/**", "/ws/**", "/graphql", "/graphiql", "/graphiql/**"};
        http
                .securityMatcher(apiPaths)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(STATELESS))

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/" + apiVersion + "/**").permitAll()
                        .requestMatchers("/graphql", "/graphiql", "/graphiql/**").permitAll()
                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devolvemos la configuración
        return http.build();
    }


    // ========================================================
// SECURITY FILTER CHAIN - SWAGGER / OPENAPI
// ========================================================
/*
 Este filtro permite el acceso libre a la documentación
 automática de la API (Swagger / OpenAPI).

 Rutas afectadas:
 - /swagger-ui/**
 - /v3/api-docs/**
 - /swagger-ui.html

 Características principales:

 1) No requiere autenticación.
    → Cualquier usuario puede acceder a la documentación.

 2) No aplica seguridad JWT.
    → Solo sirve para visualizar y probar endpoints.

 Este chain se ejecuta en segundo lugar (@Order(2)).

 ENLACE:
 http://localhost:3005/swagger-ui.html

========================================================
*/
    @Bean
    @Order(2)
    public SecurityFilterChain openapiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/swagger-ui/")
                .securityMatcher("/v3/api-docs/")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/", "/v3/api-docs/").permitAll());
        return http.build();
    }


    // ========================================================
// SECURITY FILTER CHAIN - H2 CONSOLE (DESARROLLO)
// ========================================================
/*
Este filtro permite acceder a la consola de H2 en desarrollo.

Ruta afectada:
- /h2-console/**

Qué hace:

1) Permite el acceso sin autenticación
   → .permitAll()

2) Ignora CSRF solo para H2
   → Necesario porque H2 usa formularios internos

3) Desactiva frameOptions
   → Permite que la consola se muestre en un iframe

⚠️ Solo se usa en desarrollo.
⚠️ Debe quitarse en producción.

Se ejecuta en tercer lugar (@Order(3)).
*/
// Este filtro permite el acceso a la consola de H2. Quitar en producción
    @Bean
    @Order(3)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PathRequest.toH2Console()).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }




// ========================================================
// SECURITY FILTER CHAIN - LOGIN WEB (FORMULARIO)
// ========================================================
/*
Este filtro protege la parte WEB de la aplicación
(no la API REST con JWT).

Rutas afectadas:
- /
- /public/**
- /auth/**
- /admin/**
- recursos estáticos (css, images, webjars...)

Qué hace:

1) Permite acceso libre a:
   - Página pública
   - Login
   - Recursos estáticos

2) Protege /admin/**
   → Solo usuarios con rol ADMIN

3) Activa login por formulario:
   - Página de login personalizada: /auth/login
   - Procesa login en: /auth/login-post
   - Redirige a /public si es correcto

4) Configura logout:
   - /auth/logout
   - Redirige a /public

⚠️ Este filtro es SOLO para la parte WEB.
⚠️ No afecta a la API REST con JWT.

Se ejecuta en cuarto lugar (@Order(4)).
*/
// ========================================================
    @Bean
    @Order(4)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/", "/public/**", "/auth/**", "/app/**" ,"/admin/**", "/css/**", "/images/**", "/webjars/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public", "/public/", "/public/**").permitAll()  // ← AÑADIR SIN /**
                        .requestMatchers(
                                "/",
                                "/auth/**",
                                "/webjars/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/video/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login")

                        // 🔹 OPCIÓN 1: Usar lógica personalizada tras login (cookie, auditoría, etc.)
                        .successHandler(loginSuccessHandler) // Este para usar la cockie del loginsucces

                        // 🔹 OPCIÓN 2: Redirección simple sin lógica extra
                        //.defaultSuccessUrl("/public", true)  // ← Esta para usar la cooki contador del controller public

                        .loginProcessingUrl("/auth/login-post")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/public")  // ← SIN /index
                        .permitAll());
        return http.build();
    }


    //✅ COMPONENTES AUXILIARES DE AUTENTICACIÓN
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encripta contraseñas usando algoritmo BCrypt
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService); // Proveedor de autenticación que usa UserDetailsService para cargar usuarios

        authProvider.setPasswordEncoder(passwordEncoder()); // Indica que compare las contraseñas usando BCrypt

        return authProvider; // Devuelve el proveedor configurado a Spring Security
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();   // Devuelve el gestor de autenticación principal de Spring
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();  // Crea configuración CORS

        configuration.applyPermitDefaultValues();   // Aplica valores por defecto (GET, POST, etc.)

        configuration.setAllowedOrigins(List.of("http://mifrontend.es"));   // Permite peticiones solo desde ese origen

        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));  // Métodos HTTP permitidos

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Fuente de configuración CORS basada en rutas

        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración CORS a todas las rutas

        return source; // Devuelve la configuración CORS a Spring
    }

}
