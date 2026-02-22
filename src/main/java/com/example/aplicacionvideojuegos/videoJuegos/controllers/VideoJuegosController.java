package com.example.aplicacionvideojuegos.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.services.VideoJuegoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.aplicacionvideojuegos.utils.pagination.PageResponse;
import com.example.aplicacionvideojuegos.utils.pagination.PaginationLinksUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${API_VERSION:v1}/videoJuegos")
@RestController

public class VideoJuegosController {

    private final VideoJuegoService videoJuegoService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping
    public ResponseEntity<PageResponse<VideoJuegosResponseDto>> getAll(
        @RequestParam(required = false) Optional<String> nombre,
        @RequestParam(required = false) Optional<String> cliente,
        @RequestParam(required = false) Optional<Boolean> isDeleted,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        HttpServletRequest request)
    {
        log.info("Buscando todos los videojuegos por nombre = {}, cliente = {}, isDeleted = {}", nombre, cliente, isDeleted);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

       Page<VideoJuegosResponseDto> pageResult = videoJuegoService.findAll(nombre, cliente, isDeleted, pageable);

       return ResponseEntity.ok()
               .header("Link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
               .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> getVideoJuegoById(@PathVariable Long id){
        log.info("Buscando videojuegos por id {}", id);

        return ResponseEntity.ok(videoJuegoService.findById(id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> create(@Valid @RequestBody VideoJuegosCreateDto videoJuegosCreateDto){
        log.info("Creando un nuevo videojuegos {}", videoJuegosCreateDto);
        /*if(result.hasErrors()){
            log.info("Error al crear un videojuegos {}", result.getAllErrors());
            throw new VideoJuegosBadRequest("Error al crear un videojuegos " + result.getAllErrors());
        }*/
        var saved =  videoJuegoService.save(videoJuegosCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /*
     * Actualiza una tarjeta
     *
     * @param id      de la tarjeta a actualizar
     * @param tarjetaUpdateDto con los datos a actualizar
     * @return TarjetaResponseDto actualizada
     * @throws TarjetaNotFoundException si no existe la tarjeta (404)
     * @throws TarjetaBadRequestException si la tarjeta no es correcta (400)
     */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> update( @PathVariable Long id,@Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
        log.info("Actualizando videojuegos por id={} con videojuego={}",id, videoJuegosUpdateDto);

        return ResponseEntity.ok(videoJuegoService.update(id, videoJuegosUpdateDto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
        log.info("Actualizando parcialmente un videojuego con id={} con videojuego={}" ,id, videoJuegosUpdateDto);

        return ResponseEntity.ok(videoJuegoService.update(id, videoJuegosUpdateDto));
    }

    /*
     * Borra una tarjeta por su id
     *
     * @param id de la tarjeta a borrar
     * @return ResponseEntity con status 204 No Content si se ha conseguido borradr
     * @throws TarjetaNotFoundException si no existe la tarjeta (404)
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> delete(@PathVariable Long id){
        log.info("Eliminando videojuegos por id {}", id);
        videoJuegoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */

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
