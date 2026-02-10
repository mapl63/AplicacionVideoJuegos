package com.example.aplicacionvideojuegos.web.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos.Plataforma;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import com.example.aplicacionvideojuegos.web.services.I18nService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final VideoJuegoService videoJuegoService;
    private final I18nService i18nService;

    // ===============================
    // LISTADO
    // ===============================
    @GetMapping("/videojuegos")
    public String videoJuegos(Model model,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegosPage =
                videoJuegoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegosPage);
        return "admin/videojuegos/lista";
    }

    // ===============================
    // FILTRO
    // ===============================
    @GetMapping("/videojuegos/filter")
    public String videoJuegosFilter(Model model,
                                    @RequestParam(required = false) Optional<String> nombre,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<VideoJuegosResponseDto> videoJuegosPage =
                videoJuegoService.findAll(nombre, Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", videoJuegosPage);
        return "fragments/listaJuegos";
    }

    // ===============================
    // DETALLE
    // ===============================
    @GetMapping("/videojuegos/{id}")
    public String getById(@PathVariable Long id, Model model) {
        VideoJuegos videoJuegos = videoJuegoService.buscarPorId(id).orElse(null);
        model.addAttribute("videojuego", videoJuegos);
        return "admin/videojuegos/detalle";
    }

    // ===============================
    // CREAR FORM
    // ===============================
    @GetMapping("/videojuegos/new")
    public String nuevoVideoJuegoForm(Model model) {
        model.addAttribute("videojuegos", VideoJuegosCreateDto.builder().build());
        model.addAttribute("modoEditar", false);
        model.addAttribute("plataformas", Plataforma.values());
        return "admin/videojuegos/form";
    }

    // ===============================
    // CREAR SUBMIT
    // ===============================
    @PostMapping("/videojuegos/new")
    public String nuevoVideoJuegoSubmit(@Valid @ModelAttribute("videojuegos") VideoJuegosCreateDto juego,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("plataformas", Plataforma.values());
            model.addAttribute("modoEditar", false);
            return "admin/videojuegos/form";
        }

        videoJuegoService.save(juego);

        // 🔹 MENSAJE i18n AÑADIDO
        redirectAttributes.addFlashAttribute(
                "success",
                i18nService.getMessage("videojuego.creado.ok")
        );

        return "redirect:/admin/videojuegos";
    }

    // ===============================
    // EDITAR FORM
    // ===============================
    @GetMapping("/videojuegos/{id}/edit")
    public String editarVideoJuegoForm(@PathVariable Long id, Model model) {

        VideoJuegos videoJuegoEncontrado = videoJuegoService.buscarPorId(id).orElse(null);
        if (videoJuegoEncontrado == null) {
            return "redirect:/admin/videojuegos";
        }

        VideoJuegosUpdateDto juego = VideoJuegosUpdateDto.builder()
                .nombre(videoJuegoEncontrado.getNombre())
                .precio(videoJuegoEncontrado.getPrecio())
                .fecha_lanzamiento(videoJuegoEncontrado.getFecha_lanzamiento())
                .genero(videoJuegoEncontrado.getGenero())
                .plataforma(videoJuegoEncontrado.getPlataforma())
                .edad(videoJuegoEncontrado.getEdad())
                .build();

        model.addAttribute("videojuego", juego);
        model.addAttribute("videoJuegoId", id);
        model.addAttribute("modoEditar", true);
        model.addAttribute("plataformas", Plataforma.values());

        return "admin/videojuegos/form";
    }

    // ===============================
    // EDITAR SUBMIT
    // ===============================
    @PostMapping("/videojuegos/{id}/edit")
    public String editarVideoJuegoSubmit(@PathVariable Long id,
                                         @Valid @ModelAttribute("videojuego") VideoJuegosUpdateDto juego,
                                         BindingResult result,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    i18nService.getMessage("videojuego.actualizar.error")
            );

            model.addAttribute("videoJuegoId", id);
            model.addAttribute("modoEditar", true);
            model.addAttribute("plataformas", Plataforma.values());
            return "admin/videojuegos/form";
        }

        videoJuegoService.update(id, juego);

        // 🔹 MENSAJE i18n
        redirectAttributes.addFlashAttribute(
                "success",
                i18nService.getMessage("videojuego.actualizado.ok")
        );

        return "redirect:/admin/videojuegos/" + id;
    }

    // ===============================
    // BORRAR
    // ===============================
    @PostMapping("/videojuegos/{id}/delete")
    public String borrarVideoJuego(@PathVariable Long id,
                                   @RequestParam("deleteToken") String deleteToken,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        String sessionKey = "deleteToken_" + id;
        String tokenInSession = (String) session.getAttribute(sessionKey);

        if (tokenInSession == null || !tokenInSession.equals(deleteToken)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    i18nService.getMessage("videojuego.delete.token.error")
            );
            return "redirect:/admin/videojuegos/";
        }

        session.removeAttribute(sessionKey);
        videoJuegoService.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success",
                i18nService.getMessage("videojuego.eliminado.ok")
        );

        return "redirect:/admin/videojuegos";
    }

    // ===============================
    // MODAL CONFIRMACIÓN BORRADO
    // ===============================
    @GetMapping("/videojuegos/{id}/delete/confirm")
    public String showModalBorrar(@PathVariable("id") Long id,
                                  HttpSession session,
                                  Model model) {

        Optional<VideoJuegos> videoJuego = videoJuegoService.buscarPorId(id);

        if (videoJuego.isEmpty()) {
            return "redirect:/videojuegos?error=true";
        }

        String deleteMessage = i18nService.getMessage(
                "videojuegos.borrar.mensaje",
                new Object[]{videoJuego.get().getNombre()}
        );

        String token = UUID.randomUUID().toString();
        String sessionKey = "deleteToken_" + id;
        session.setAttribute(sessionKey, token);

        model.addAttribute("deleteUrl", "/admin/videojuegos/" + id + "/delete");
        model.addAttribute("deleteToken", token);
        model.addAttribute("deleteTitle", i18nService.getMessage("videojuegos.borrar.titulo"));
        model.addAttribute("deleteMessage", deleteMessage);

        return "fragments/deleteModal";
    }
}
