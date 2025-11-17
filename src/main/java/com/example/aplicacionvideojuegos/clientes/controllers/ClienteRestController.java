package com.example.aplicacionvideojuegos.clientes.controllers;

import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.services.ClienteService;
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
@RestController
@RequestMapping("api/${API_VERSION:v1}/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;

    @GetMapping()
    public ResponseEntity<List<Cliente>> getAll(@RequestParam(required = false) String nombre) {
        log.info("Buscando todos los clientes con nombre: {}", nombre);
        return ResponseEntity.ok(clienteService.findAll(nombre));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        log.info("Buscando el cliente con id: {}", id);
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Cliente> create(@Valid @RequestBody ClienteRequestDto clienteRequestDto) {
        log.info("Creando un nuevo cliente: {}", clienteRequestDto);
        var saved = clienteService.save(clienteRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @Valid @RequestBody ClienteRequestDto clienteRequestDto) {
        log.info("Actualizando el cliente con id: {} con los datos: {}", id, clienteRequestDto);

        return ResponseEntity.ok(clienteService.update(id, clienteRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Eliminando el cliente con id: {}", id);
        clienteService.deleteById(id);
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
