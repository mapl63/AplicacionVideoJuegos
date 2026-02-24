package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.users.models.Role;
import com.example.aplicacionvideojuegos.rest.users.models.User;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;

import static org.mockito.Mockito.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalControllerAdviceTest {

    private final GlobalControllerAdvice advice = new GlobalControllerAdvice();

    @Test
    void getAppName() {
    }

    @Test
    void getAppDescription() {
    }

    // ======================================================
// 🔵 TEST getCurrentUser (TRUE)
// ======================================================
    @Test
    @DisplayName("getCurrentUser devuelve usuario autenticado")
    void getCurrentUserTrue() {

        User user = User.builder().build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        User result = advice.getCurrentUser(auth);

        assertThat(result).isEqualTo(user);
    }

    // ======================================================
// 🔵 TEST getCurrentUser (FALSE)
// ======================================================
    @Test
    @DisplayName("getCurrentUser devuelve null si no hay usuario")
    void getCurrentUserFalse() {

        User result = advice.getCurrentUser(null);

        assertThat(result).isNull();
    }

// ======================================================
// 🔵 TEST isAuthenticated (TRUE)
// ======================================================
// ✅ Devuelve true si hay usuario autenticado
    @Test
    @DisplayName("isAuthenticated devuelve true si hay usuario válido")
    void isAuthenticatedTrue() {

        User user = User.builder().build(); // 👈 Usuario simple

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        boolean result = advice.isAuthenticated(auth);

        assertThat(result).isTrue();
    }

// ======================================================
// 🔵 TEST isAuthenticated (FALSE)
// ======================================================
// ✅ Devuelve false si authentication es null
    @Test
    @DisplayName("isAuthenticated devuelve false si no hay auth")
    void isAuthenticatedFalse() {

        boolean result = advice.isAuthenticated(null);

        assertThat(result).isFalse();
    }

// ======================================================
// 🔵 TEST isAdmin (TRUE)
// ======================================================
// ✅ Devuelve true si el usuario tiene rol ADMIN
    @Test
    @DisplayName("isAdmin devuelve true si tiene rol ADMIN")
    void isAdminTrue() {

        User user = User.builder()
                .roles(Set.of(Role.ADMIN))
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        boolean result = advice.isAdmin(auth);

        assertThat(result).isTrue();
    }

// ======================================================
// 🔵 TEST isAdmin (FALSE)
// ======================================================
// ✅ Devuelve false si el usuario NO tiene rol ADMIN
    @Test
    @DisplayName("isAdmin devuelve false si no tiene rol ADMIN")
    void isAdminFalse() {

        User user = User.builder()
                .roles(Set.of(Role.USER)) // 👈 Solo rol USER
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        boolean result = advice.isAdmin(auth);

        assertThat(result).isFalse(); // 👈 No es admin
    }

    // ======================================================
// 🔵 TEST getUsername
// ======================================================
    @Test
    @DisplayName("getUsername devuelve nombre completo")
    void getUsername() {

        User user = User.builder()
                .nombre("Mario")
                .apellidos("Lopez")
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        String result = advice.getUsername(auth);

        assertThat(result).isEqualTo("Mario Lopez");
    }

    // ======================================================
// 🔵 TEST getUserRoles
// ======================================================
    @Test
    @DisplayName("getUserRoles devuelve roles concatenados")
    void getUserRoles() {

        User user = User.builder()
                .roles(Set.of(Role.ADMIN, Role.USER))
                .build();

        var auth = new UsernamePasswordAuthenticationToken(user, null, Set.of());

        String result = advice.getUserRoles(auth);

        assertThat(result).contains("ADMIN");
        assertThat(result).contains("USER");
    }

    // ======================================================
// 🔵 TEST CSRF METHODS
// ======================================================
    @Test
    @DisplayName("CSRF methods devuelven valores del token")
    void csrfMethods() {

        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken token = mock(CsrfToken.class);

        when(request.getAttribute(CsrfToken.class.getName())).thenReturn(token);
        when(token.getToken()).thenReturn("12345");
        when(token.getParameterName()).thenReturn("_csrf");
        when(token.getHeaderName()).thenReturn("X-CSRF-TOKEN");

        String csrfToken = advice.getCsrfToken(request);
        String csrfParam = advice.getCsrfParamName(request);
        String csrfHeader = advice.getCsrfHeaderName(request);

        assertThat(csrfToken).isEqualTo("12345");
        assertThat(csrfParam).isEqualTo("_csrf");
        assertThat(csrfHeader).isEqualTo("X-CSRF-TOKEN");
    }

    // ======================================================
// 🔵 TEST getCurrentDateTime
// ======================================================
    @Test
    @DisplayName("getCurrentDateTime devuelve fecha actual")
    void currentDateTime() {

        var dateTime = advice.getCurrentDateTime();

        assertThat(dateTime).isNotNull();
    }

    // ======================================================
// 🔵 TEST getCurrentYear
// ======================================================
    @Test
    @DisplayName("getCurrentYear devuelve año actual")
    void currentYear() {

        int year = advice.getCurrentYear();

        assertThat(year).isGreaterThan(2020);
    }

    // ======================================================
// 🔵 TEST getCurrentMonth
// ======================================================
    @Test
    @DisplayName("getCurrentMonth devuelve mes en español")
    void currentMonth() {

        String month = advice.getCurrentMonth();

        assertThat(month).isNotBlank();
    }
}