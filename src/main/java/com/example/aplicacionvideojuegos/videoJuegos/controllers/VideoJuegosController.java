package com.example.aplicacionvideojuegos.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.services.VideoJuegoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${api.version}/videoJuegos")
@RestController

public class VideoJuegosController {
    private final VideoJuegoService videoJuegoService;

    @GetMapping
    public ResponseEntity<List<VideoJuegosResponseDto>> getAllVideoJuegos(@RequestParam(required = false) String nombre,
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
    public ResponseEntity<VideoJuegosResponseDto> create(@Valid @RequestBody VideoJuegosCreateDto videoJuegosCreateDto){
        log.info("Creando un nuevo videojuegos {}", videoJuegosCreateDto);
        /*if(result.hasErrors()){
            log.info("Error al crear un videojuegos {}", result.getAllErrors());
            throw new VideoJuegosBadRequest("Error al crear un videojuegos " + result.getAllErrors());
        }*/
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
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}
