package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;

import jakarta.servlet.http.Cookie;                // 👈 Necesario para trabajar con cookies
import jakarta.servlet.http.HttpServletRequest;   // 👈 Para leer cookies
import jakarta.servlet.http.HttpServletResponse;  // 👈 Para crear/enviar cookies


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {

    private final VideoJuegoService videoJuegoService;

    // ======================================================
    // 🍪 CÓMO CREAR Y LEER UNA COOKIE EN SPRING BOOT
    // ======================================================
    // 1️⃣ La cookie se crea en un Controller.
    // 2️⃣ Necesitamos HttpServletResponse para enviarla.
    // 3️⃣ Necesitamos HttpServletRequest para leerla.
    // 4️⃣ Se guarda en el navegador del usuario.
    // ======================================================
/*
    @GetMapping("/ejemplo-cookie")
    public String ejemploCookie(HttpServletRequest request,   // 👈 Para leer cookies
                                HttpServletResponse response, // 👈 Para crear/enviar cookies
                                Model model) {

        // =============================
        // 🔹 CREAR COOKIE
        // =============================

        Cookie cookie = new Cookie("miCookie", "valorEjemplo"); // 👈 Nombre y valor
        cookie.setPath("/");        // 👈 Disponible en toda la aplicación
        cookie.setMaxAge(3600);     // 👈 Duración: 1 hora (en segundos)
        response.addCookie(cookie); // 👈 Se envía al navegador

        // =============================
        // 🔹 LEER COOKIE
        // =============================

        String valor = "sinValor";  // 👈 Valor por defecto

        if (request.getCookies() != null) { // 👈 Comprobamos si hay cookies
            for (Cookie c : request.getCookies()) {
                if ("miCookie".equals(c.getName())) { // 👈 Buscamos nuestra cookie
                    valor = c.getValue();             // 👈 Leemos su valor
                }
            }
        }

        model.addAttribute("valorCookie", valor); // 👈 Pasamos el valor a la vista

        return "index"; // 👈 Vista Pebble (index.peb.html)
    }

*/
    @GetMapping({"", "/", "/index" })
    public String index(Model model,
                        HttpServletRequest request,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegoPage = videoJuegoService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegoPage);

        // 🔹 LEER COOKIE visitasApp
        int visitas = 0;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("visitasApp".equals(c.getName())) {
                    try {
                        visitas = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        model.addAttribute("visitas", visitas); // 🔹 Pasamos valor a Pebble

        return "index";
    }

    @GetMapping("/set-lang")
    public String setLang(@RequestParam String lang,
                          HttpServletResponse response,
                          HttpServletRequest request){

        if (!lang.equals("es") && !lang.equals("en")) {
            lang = "es"; // Valor predeterminado si el idioma no es válido
        }

        Cookie cookie = new Cookie("lang", lang);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");

        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/public/index");
    }



// ======================================================
// 🍪 COOKIE CONTADOR POR VISITA (NO POR LOGIN)
// ======================================================
// Este contador aumenta cada vez que el usuario entra
// en la página /public.
// No depende del login.
// ======================================================
/*
    @GetMapping({"", "/", "/index"})
    public String index(Model model,
                        HttpServletRequest request,      // 👈 Para leer cookies
                        HttpServletResponse response,    // 👈 Para crear/actualizar cookies
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size) {

        // =============================
        // PARTE NORMAL DE LA PÁGINA
        // =============================

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegoPage =
                videoJuegoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegoPage);

        // =============================
        // 🍪 CONTADOR DE VISITAS
        // =============================

        int visitas = 0; // 👈 Valor inicial del contador

        // 🔹 LEER COOKIE SI YA EXISTE
        if (request.getCookies() != null) {  // 👈 Comprobamos si el navegador envía cookies
            for (Cookie c : request.getCookies()) {
                if ("contadorVisitas".equals(c.getName())) { // 👈 Buscamos nuestra cookie
                    try {
                        visitas = Integer.parseInt(c.getValue()); // 👈 Convertimos valor a número
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        // 🔹 AUMENTAR CONTADOR
        visitas++; // 👈 Cada vez que entra a la página suma 1

        // 🔹 CREAR O ACTUALIZAR COOKIE
        Cookie nueva = new Cookie("contadorVisitas", String.valueOf(visitas));
        nueva.setPath("/");                 // 👈 Disponible en toda la aplicación
        nueva.setMaxAge(365 * 24 * 60 * 60); // 👈 Dura 1 año
        response.addCookie(nueva);          // 👈 Se envía al navegador

        // 🔹 PASAR VALOR A LA VISTA
        model.addAttribute("visitas", visitas);

        return "index";
    }
*/
}
