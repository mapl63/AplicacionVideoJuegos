package com.example.aplicacionvideojuegos.rest.users.controllers;



import com.example.aplicacionvideojuegos.rest.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.rest.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.rest.users.dto.UserResponse;
import com.example.aplicacionvideojuegos.rest.users.models.User;
import com.example.aplicacionvideojuegos.rest.users.services.UserService;
import com.example.aplicacionvideojuegos.utils.pagination.PageResponse;
import com.example.aplicacionvideojuegos.utils.pagination.PaginationLinksUtils;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/${API_VERSION:v1}/users")
@PreAuthorize("hasRole('USER')")
public class UsersRestController {

    private final UserService usersService;
    private final PaginationLinksUtils paginationLinksUtils;
    private final VideoJuegoService videoJuegoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Metodo findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                username, email, isDeleted, page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());

        Page<UserResponse> pageResult = usersService.findAll(
                username,
                email,
                isDeleted,
                PageRequest.of(page, size, sort)
        );

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("Buscando un usuario con el rol de admin por el metodo findById: id={}", id);
        return ResponseEntity.ok(usersService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest){
        log.info("Creando un nuevo usuario con el rol de admin por el metodo createUser: userRequest={}", userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest){
        log.info("Actualizando un usuario con el rol de admin por el metodo updateUser: id={}, userRequest={}", id, userRequest);

        return ResponseEntity.ok(usersService.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        log.info("Eliminando un usuario con el rol de admin por el metodo deleteUser: id={}", id);

        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user){
        log.info("Obteniendo usuario");
        return ResponseEntity.ok(usersService.findById(user.getId()));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequest userRequest){
        log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
        return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
    }

    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("deleteMe: user: {}", user);
        usersService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/videojuegos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<VideoJuegosResponseDto>> getVideoJuegosByUsuarios(
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction
    ){
        log.info("Obteniendo los videojuegos del usuario con id: {}", user.getId());

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(PageResponse.of(videoJuegoService.findByUsuarioId(user.getId(), pageable), sortBy, direction));
    }

    @GetMapping("/me/videojuegos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VideoJuegosResponseDto> getVideoJuego(
        @AuthenticationPrincipal User user,
        @PathVariable("id") Long idVideojuego
    ){
        log.info("Obteniendo el videojuego con id: {}", idVideojuego);
        return ResponseEntity.ok(videoJuegoService.findById(idVideojuego));
    }

    @PostMapping("/me/videojuegos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VideoJuegosResponseDto> saveVideoJuego(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody VideoJuegosCreateDto videoJuegosCreateDto
    ){
      log.info("Creando un videojuego: {}", videoJuegosCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(videoJuegoService.save(videoJuegosCreateDto));
    }

    @PutMapping("/me/videojuegos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<VideoJuegosResponseDto> updateVideoJuego(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idVideojuego,
            @Valid @RequestBody VideoJuegosUpdateDto videoJuego
    ){
        log.info("Actualizando el videojuego con id: {}", idVideojuego);
        return ResponseEntity.ok(videoJuegoService.update(idVideojuego, videoJuego));
    }

    @DeleteMapping("/me/videojuegos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteVideoJuego(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long idVideojuego
    ){
        log.info("Eliminando el videojuego con id: {}", idVideojuego);
        videoJuegoService.deleteById(idVideojuego);
        return ResponseEntity.noContent().build();
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
