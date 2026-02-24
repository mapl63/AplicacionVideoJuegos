package com.example.aplicacionvideojuegos.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 🍪 COOKIE (cliente):
// Información persistente almacenada en el navegador del usuario.
// Puede durar días, meses o años según el Max-Age.

// 🗂️ HTTP SESSION (servidor):
// Información temporal almacenada en el servidor para el usuario autenticado.
// Se identifica mediante la cookie automática JSESSIONID.

@Component // 👈 Registramos este handler como componente de Spring
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String COOKIE_NAME = "visitasApp"; // 👈 Nombre de la cookie personalizada
    private static final int MAX_AGE = 365 * 24 * 60 * 60;   // 👈 Duración: 1 año en segundos

    public LoginSuccessHandler() {
        setDefaultTargetUrl("/public"); // 👈 Redirección por defecto tras login
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        int val = 0; // 👈 Valor inicial del contador de visitas

        Cookie[] cookies = request.getCookies(); // 👈 Obtenemos las cookies enviadas por el navegador

        if (cookies != null) {
            for (Cookie c : cookies) { // 👈 Recorremos cookies existentes
                if (COOKIE_NAME.equals(c.getName())) { // 👈 Buscamos nuestra cookie "visitasApp"
                    try {
                        val = Integer.parseInt(c.getValue()); // 👈 Leemos su valor actual
                    } catch (NumberFormatException ignored) {} // 👈 Si falla, lo ignoramos
                }
            }
        }

        val++; // 👈 Incrementamos el contador de inicios de sesión

        Cookie newCookie = new Cookie(COOKIE_NAME, Integer.toString(val));
        // 👈 Creamos nueva cookie con el valor actualizado

        newCookie.setPath("/"); // 👈 Disponible en toda la aplicación
        newCookie.setMaxAge(MAX_AGE); // 👈 Persistente durante 1 año
        newCookie.setHttpOnly(false); // 👈 Accesible desde JavaScript
        newCookie.setSecure(request.isSecure()); // 👈 Solo HTTPS si la petición es segura

        response.addCookie(newCookie); // 👈 Enviamos la cookie al navegador

        super.onAuthenticationSuccess(request, response, authentication);
        // 👈 Continúa el flujo normal de Spring Security (redirect)
    }
}