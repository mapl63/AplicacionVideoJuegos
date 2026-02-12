package com.example.aplicacionvideojuegos.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

    @Value("${api.version}")
    private String apiVersion;




    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] apiPaths = { "/api/**", "/error/**", "/ws/**", "/graphql", "/graphiql", "/graphiql/**" };
        http
                .securityMatcher(apiPaths)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(
                        manager -> manager.sessionCreationPolicy(STATELESS))

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/" + apiVersion + "/**").permitAll()
                        .requestMatchers("/graphql", "/graphiql","/graphiql/**").permitAll()
                        .anyRequest().authenticated())

                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devolvemos la configuración
        return http.build();
    }

    // Este filtro permite el acceso a la documentación OpenAPI
    @Bean
    @Order(2)
    public SecurityFilterChain openapiFilterChain(HttpSecurity http) throws Exception {
        String[] swaggerPaths = { "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"};

            http
                .securityMatcher(swaggerPaths)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(swaggerPaths).permitAll());
        return http.build();
    }


    // Este filtro permite el acceso a la consola de H2. Quitar en producción
        @Bean
        @Order(3)
        @Profile("dev")
        public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(PathRequest.toH2Console())
                    .authorizeHttpRequests(auth ->
                            auth.requestMatchers(PathRequest.toH2Console()).permitAll())
                    .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                    .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
            return http.build();
        }

    // Este filtro permite el acceso a la consola de H2. Quitar en producción
        @Bean
        @Order(4)
        public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
            http
                    // Deshabilitamos CSRF
                    //.csrf(AbstractHttpConfigurer::disable)
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
                            .defaultSuccessUrl("/public", true)  // ← SIN /index
                            .loginProcessingUrl("/auth/login-post")
                            .permitAll())
                    .logout(logout -> logout
                            .logoutUrl("/auth/logout")
                            .logoutSuccessUrl("/public")  // ← SIN /index
                            .permitAll());
            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
            authProvider.setPasswordEncoder(passwordEncoder());
            return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                throws Exception {
            return config.getAuthenticationManager();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.applyPermitDefaultValues();
            configuration.setAllowedOrigins(List.of("http://mifrontend.es"));
            configuration.setAllowedMethods(List.of( "GET", "POST", "DELETE", "PUT", "PATCH"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**",configuration);
            return source;
        }
}
