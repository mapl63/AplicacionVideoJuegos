package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/videojuegos")
public class VideoJuegosWebController {

    private final VideoJuegoService videoJuegosService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        VideoJuegosResponseDto videoJuego = videoJuegosService.findById(id);
        model.addAttribute("videoJuego", videoJuego);
        return "videojuegos/videojuegoDetalle";

    }

}
