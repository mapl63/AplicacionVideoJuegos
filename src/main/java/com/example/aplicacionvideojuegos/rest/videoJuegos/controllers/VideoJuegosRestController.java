package com.example.aplicacionvideojuegos.rest.videoJuegos.controllers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import jakarta.persistence.PostRemove;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "VideoJuegos", description = "Endpoint de VideoJuegos de nuestra API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/${API_VERSION:v1}/videoJuegos")
@RestController

public class VideoJuegosRestController {

    private final VideoJuegoService videoJuegoService;
    private final PaginationLinksUtils paginationLinksUtils;



    @Operation(summary = "Obtiene una lista paginada de todos los videojuegos", tags = {"videoJuegos"})
    @Parameters({
        @Parameter(name = "nombre", description = "Nombre del VideoJuego", example = ""),
        @Parameter(name = "cliente", description = "Cliente del videoJuego", example = ""),
        @Parameter(name = "isDeleted", description = "si está borrada o no", required = false),
        @Parameter(name = "page", description = "Número de página", example = "0"),
        @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
        @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
        @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Página de videoJuegos")

    })

    @GetMapping
    public ResponseEntity<PageResponse<VideoJuegosResponseDto>> getAll(
        @RequestParam(required = false) Optional<String> nombre,
        @RequestParam(required = false) Optional<String> cliente,
        @RequestParam(required = false) Optional<Boolean> isDeleted,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        HttpServletRequest request) {
        log.info("Buscando todos los videojuegos por nombre = {}, cliente = {}, isDeleted = {}", nombre, cliente, isDeleted);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

       Page<VideoJuegosResponseDto> pageResult = videoJuegoService.findAll(nombre, cliente, isDeleted, pageable);

       return ResponseEntity.ok()
               .header("Link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
               .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /*
     * Obtiene un videojuego por su id
     *
     * @param id del videojuego a buscar
     * @return VideoJuegosResponseDto del videojuego encontrado
     * @throws VideoJuegosNotFoundException si no existe el videojuego (404)
     */

    @Operation(summary = "Obtiene un videojuego por su id", description = "obtiene un videoJuego por su id")
    @Parameters({
        @Parameter(name = "id", description = "Identificador único del videojuego", required = true, example = "1")
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Videojuego encontrado"),
        @ApiResponse(responseCode = "404", description = "Videojuego no encontrado")
    })

    @GetMapping("/{id}")
    public ResponseEntity<VideoJuegosResponseDto> getVideoJuegoById(@PathVariable Long id){
        log.info("Buscando videojuegos por id {}", id);

        return ResponseEntity.ok(videoJuegoService.findById(id));
    }

    /*
     * Crea un nuevo videojuego
     *
     * @param videoJuegosCreateDto con los datos del nuevo videojuego
     * @return VideoJuegosResponseDto del videojuego creado
     * @throws VideoJuegosBadRequestException si el videojuego no es correcto (400)
     */

    @Operation(summary = "Crea un nuevo videojuego", description = "Crea un nuevo videojuego")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "VideoJuego a crear", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Videojuego creado"),
        @ApiResponse(responseCode = "400", description = "Videojuego no válido")
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> create(@Valid @RequestBody VideoJuegosCreateDto videoJuegosCreateDto){
        log.info("Creando un nuevo videojuegos {}", videoJuegosCreateDto);

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

    @Operation(summary = "Actualiza un videojuego por su id", description = "Actualiza un videojuego por su id")
    @Parameters({
        @Parameter(name = "id", description = "Identificador único del videojuego", required = true, example = "1")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Videojuego a actualizar", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Videojuego actualizado"),
        @ApiResponse(responseCode = "400", description = "Videojuego no válido"),
        @ApiResponse(responseCode = "404", description = "Videojuego no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> update( @PathVariable Long id,@Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
        log.info("Actualizando videojuegos por id={} con videojuego={}",id, videoJuegosUpdateDto);

        return ResponseEntity.ok(videoJuegoService.update(id, videoJuegosUpdateDto));
    }

    /*
     * Actualiza parcialmente una tarjeta
     *
     * @param id      de la tarjeta a actualizar
     * @param tarjetaUpdateDto con los datos a actualizar
     * @return TarjetaResponseDto actualizada
     * @throws TarjetaNotFoundException si no existe la tarjeta (404)
     * @throws TarjetaBadRequestException si la tarjeta no es correcta (400)
     */

    @Operation(summary = "Actualiza parcialmente un videojuego por su id", description = "Actualiza parcialmente un videojuego por su id")
    @Parameters({
        @Parameter(name = "id", description = "Identificador único del videojuego", required = true, example = "1")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Videojuego a actualizar parcialmente", required = true)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Videojuego actualizado"),
        @ApiResponse(responseCode = "400", description = "Videojuego no válido"),
        @ApiResponse(responseCode = "404", description = "Videojuego no encontrado")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VideoJuegosResponseDto> updatePartial(@PathVariable Long id,
                                                                @Valid @RequestBody VideoJuegosUpdateDto videoJuegosUpdateDto){
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
    @Operation(summary = "Borra un videojuego por su id", description = "Borra un videojuego por su id")
    @Parameters({
        @Parameter(name = "id", description = "Identificador único del videojuego", required = true, example = "1")
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Videojuego borrado"),
        @ApiResponse(responseCode = "404", description = "Videojuego no encontrado")
    })
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
