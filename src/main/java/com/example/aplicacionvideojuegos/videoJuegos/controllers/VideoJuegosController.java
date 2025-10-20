package com.example.aplicacionvideojuegos.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosBadRequest;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.services.VideoJuegoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("api/${api.version}/videoJuegos")
@RestController

public class VideoJuegosController {
    private final VideoJuegoService videoJuegoService;

    @Autowired
    public VideoJuegosController(VideoJuegoService videoJuegoService) {
        this.videoJuegoService = videoJuegoService;
    }

    @GetMapping
    public ResponseEntity<List<VideoJuegos>> getAllVideoJuegos(@RequestParam(required = false) String nombre,
                                                               @RequestParam(required = false) String genero,
                                                               @RequestParam(required = false) VideoJuegos.Plataforma plataforma) {
        log.info("Buscando videoJuegos por el nombre {} y genero {} y plataforma {}", nombre, genero, plataforma);
        return ResponseEntity.ok(videoJuegoService.findAll(nombre, genero, plataforma));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> getVideoJuegoById(@PathVariable Long id){
        log.info("Buscando videojuegos por id {}", id);

        return ResponseEntity.ok(videoJuegoService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<VideoJuegosResponseDto> create(@Valid @RequestBody VideoJuegosCreateDto videoJuegosCreateDto, BindingResult result){
        log.info("Creando un nuevo videojuegos {}", videoJuegosCreateDto);
        if(result.hasErrors()){
            log.info("Error al crear un videojuegos {}", result.getAllErrors());
            throw new VideoJuegosBadRequest("Error al crear un videojuegos " + result.getAllErrors());
        }
        var saved =  videoJuegoService.save(videoJuegosCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);


    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> update( @PathVariable Long id,@Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
        log.info("Actualizando videojuegos por id={} con videojuego={}",id, videoJuegosUpdateDto);
        return ResponseEntity.ok(videoJuegoService.update(id, videoJuegosUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
        log.info("Actualizando parcialmente un videojuego con id={} con videojuego={}" ,id, videoJuegosUpdateDto);
        return ResponseEntity.ok(videoJuegoService.update(id, videoJuegosUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> delete(@PathVariable Long id){
        log.info("Eliminando videojuegos por id {}", id);
        videoJuegoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldNAme = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldNAme, errorMessage);
        });
        return errors;
    }
}
