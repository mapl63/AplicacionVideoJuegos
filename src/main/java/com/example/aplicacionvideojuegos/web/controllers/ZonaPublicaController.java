package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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


    @GetMapping({"", "/", "/index" })
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegoPage = videoJuegoService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegoPage);
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

}
