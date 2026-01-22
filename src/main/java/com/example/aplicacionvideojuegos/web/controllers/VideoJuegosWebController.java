package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
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
        model.addAttribute("modoEditar", false);
        return "/videojuegos/form";
    }

    @PostMapping("/new")
    public String nuevoVideoJuegoSubmit(@Valid @ModelAttribute VideoJuegosCreateDto videoJuego,
                                        BindingResult bindingResult) {
    log.info("Datos recibidos del formulario: {}", videoJuego);
    //Si no tiene errores...
        if (bindingResult.hasErrors()) {
            log.info("hay errores en la validación");
            return "/videojuegos/form";
        }else{
            videoJuegosService.save(videoJuego);
            return "redirect:/videojuegos/lista";
        }
    }

    @GetMapping("/{id}/edit")
    public String editarVideoJuegoForm(@PathVariable Long id, Model model) {
        VideoJuegosResponseDto videoJuegoEncontrado = videoJuegosService.findById(id);
        if(videoJuegoEncontrado == null){
            return "redirect:/videojuegos/new";
        }else{
            VideoJuegosUpdateDto videoJuego = VideoJuegosUpdateDto.builder()
                    .nombre(videoJuegoEncontrado.getNombre())
                    .precio(videoJuegoEncontrado.getPrecio())
                    .fecha_lanzamiento(videoJuegoEncontrado.getFecha_lanzamiento())
                    .genero(videoJuegoEncontrado.getGenero())
                    .plataforma(videoJuegoEncontrado.getPlataforma())
                    .edad(videoJuegoEncontrado.getEdad())
                    .build();

            model.addAttribute("videojuego", videoJuego);
            model.addAttribute("videojuegoId", id);
            model.addAttribute("modoEditar", true);
            return "/videojuegos/form";
        }
    }

    @PostMapping("/{id}/edit")
    public String editarVideoJuegoSubmit(@PathVariable("id") Long id,
                                         @Valid @ModelAttribute("videojuego") VideoJuegosUpdateDto videoJuego,
                                         BindingResult result,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("error",
                    "Ha ocurrido un error al actualizar el videojuego.");
            model.addAttribute("videojuegoId", id);
            model.addAttribute("modoEditar", true);
            return "/videojuegos/form";
        }

        videoJuegosService.update(id, videoJuego);
        redirectAttributes.addFlashAttribute("message",
                "Videojuego actualizado correctamente.");
        return "redirect:/videojuegos/{id}";
    }

    @GetMapping("/{id}/delete")
    public String borrarVideoJuego(@PathVariable Long id){
        videoJuegosService.deleteById(id);
        return "redirect:/videojuegos/lista";
    }


}
