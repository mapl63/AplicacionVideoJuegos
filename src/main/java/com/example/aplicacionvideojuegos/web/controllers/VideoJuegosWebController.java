package com.example.aplicacionvideojuegos.web.controllers;


import com.example.aplicacionvideojuegos.rest.users.models.User;
import com.example.aplicacionvideojuegos.rest.users.services.UserService;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;


import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class VideoJuegosWebController {

    private final VideoJuegoService videoJuegosService;
    private final UserService userService;

    @GetMapping("/misVideoJuegos")
    public String list(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size
    ){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> usuario = userService.findByUsername(username);

        if(usuario.isEmpty()){
            model.addAttribute("page", Page.empty());
            return "app/videojuegos/lista";
        }

        Long usuarioId = usuario.get().getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<VideoJuegosResponseDto> juegos =
                videoJuegosService.findByUsuarioId(usuarioId, pageable);

        model.addAttribute("page", juegos);

        return "app/videojuegos/lista";
    }

    @GetMapping("/misVideoJuegos/{id}")
    public String getById(@PathVariable Long id, Model model) {
        VideoJuegos videoJuegos = videoJuegosService.buscarPorId(id).orElse(null);

        model.addAttribute("videojuego", videoJuegos);

        return "app/videojuegos/detalle";

    }
}