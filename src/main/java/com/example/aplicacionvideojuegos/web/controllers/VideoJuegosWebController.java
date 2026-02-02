package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.users.models.User;
import com.example.aplicacionvideojuegos.rest.users.services.UserService;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class VideoJuegosWebController {

    private final VideoJuegoService videoJuegosService;
    private final UserService userService;

    @ModelAttribute("videojuegos")
    public List<VideoJuegos> misVideoJuegos() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> usuario = userService.findByUsername(username);

        if (usuario.isEmpty()) {
            return List.of();
        } else {
            return videoJuegosService.buscarPorUsuarioId(usuario.get().getId());
        }
    }

    @GetMapping("/misVideoJuegos")
    public String list(){
        return "app/videojuegos/lista";
    }

    @GetMapping("/misVideoJuegos/{id}")
    public String getById(@PathVariable Long id, Model model) {
        VideoJuegos videoJuego = videoJuegosService.buscarPorId(id).orElse(null);

        model.addAttribute("videoJuegos", videoJuego);

        return "/app/videojuegos/videoJuegoDetalle";

    }




}
