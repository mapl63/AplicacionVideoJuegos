package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping({"","/","/lista"})
    public String lista(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegosPage = videoJuegosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegosPage);
        return "videojuegos/lista";
    }

    @GetMapping("/new")
    public String nuevoVideoJuegoForm(Model model) {
        model.addAttribute("videojuegos", VideoJuegosCreateDto.builder().build());
        return "/videojuegos/form";
    }

    @PostMapping("/new")
    public String nuevoVideoJuegoSubmit(@Valid @ModelAttribute VideoJuegosCreateDto videoJuego, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/videojuegos/form";
        }else{
            videoJuegosService.save(videoJuego);
            return "redirect:/videojuegos/lista";
        }
    }

}
