package com.example.aplicacionvideojuegos.rest.videoJuegos.dto;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.validators.ValidPlataforma;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
@Schema(description = "VideoJuegos a crear")
public class VideoJuegosCreateDto {

    @NotBlank(message = "El cliente no puede estar vacío")
    @Schema(description = "Cliente que crea el videojuego", example = "Juan Perez")
    private final String cliente;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del videojuego", example = "Avatar: Frontiers of Pandora")
    private final String nombre;

    @Positive(message = "El precio no puede ser negativo ni 0")
    @Schema(description = "Precio del videojuego", example = "69.99")
    private final Double precio;

    @Schema(description = "Fecha de lanzamiento del videojuego", example = "2024-12-19")
    private final LocalDate fecha_lanzamiento;


    @NotBlank(message = "El genero no puede estar vacío")
    @Schema(description = "Género del videojuego", example = "Acción/Aventura")
    private final String genero;

    @ValidPlataforma
    @Schema(description = "Plataforma del videojuego", example = "PC")
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad no puede ser negativa.")
    @Schema(description = "Edad recomendada para el videojuego", example = "16")
    private final Integer edad;

}
