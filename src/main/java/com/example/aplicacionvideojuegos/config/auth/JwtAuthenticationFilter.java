package com.example.aplicacionvideojuegos.config.auth;

import com.example.aplicacionvideojuegos.rest.auth.services.jwt.JwtService;
import com.example.aplicacionvideojuegos.rest.auth.services.users.AuthUsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JwtAuthenticationFilter
// Lee token → valida → autentica.

@Slf4j // Activa logs
@RequiredArgsConstructor // Crea constructor con dependencias finales
@Component // Marca la clase como componente de Spring
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Filtro que se ejecuta una vez por petición

    private final JwtService jwtService;    // Servicio que genera y valida el JWT

    private final AuthUsersService authUsersService;    // Servicio que carga usuarios desde la base de datos

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Iniciando el filtro de autenticación"); // Log de inicio

        final String authHeader = request.getHeader("Authorization");   // Obtiene el header Authorization

        final String jwt; // Variable donde guardaremos el token
        UserDetails userDetails = null; // Usuario autenticado
        String userName = null; // Username extraído del token

        // Si no hay header o no empieza por "Bearer "
        if (!StringUtils.hasText(authHeader) ||
                !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {

            log.info("No se ha encontrado cabecera de autenticación, se ignora");
            filterChain.doFilter(request, response);    // Continúa la petición sin autenticar
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        jwt = authHeader.substring(7);  // Extrae el token quitando "Bearer "

        try {
            userName = jwtService.extractUserName(jwt); // Extrae el username del token
        } catch (Exception e) {
            log.info("Token no válido");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token no autorizado o no válido");
            return; // Devuelve 401 si el token es inválido
        }

        log.info("Usuario autenticado: {}", userName);

        if (StringUtils.hasText(userName) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {   // Si hay username y no hay autenticación previa

            try {
                userDetails = authUsersService.loadUserByUsername(userName);    // Carga el usuario real desde la base de datos
            } catch (Exception e) {
                log.info("Usuario no encontrado: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Usuario no autorizado");
                return; // Devuelve 401 si el usuario no existe
            }

            log.info("Usuario encontrado: {}", userDetails);

            if (jwtService.isTokenValid(jwt, userDetails)) {    // Verifica firma y expiración del token

                log.info("JWT válido");

                SecurityContext context =
                        SecurityContextHolder.createEmptyContext(); // Crea un contexto de seguridad vacío

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());  // Crea objeto de autenticación con roles

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));    // Añade detalles de la petición

                context.setAuthentication(authToken);   // Guarda la autenticación en el contexto

                SecurityContextHolder.setContext(context);  // Spring ya considera al usuario autenticado
            }
        }

        filterChain.doFilter(request, response);    // Continúa la petición hacia el controller
    }
}