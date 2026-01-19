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
@Schema(description = "VideoJuegos a actualizar")
public class VideoJuegosUpdateDto {

    @NotBlank(message = "El nuevo nombre no puede estar vacío")
    @Schema(description = "Nombre del videojuego", example = "The Legend of Zelda: Breath of the Wild")
    private final String nombre;

    @Positive(message = "El nuevo precio no puede ser negativo")
    @Schema(description = "Precio del videojuego", example = "59.99")
    private final Double precio;

    @Schema(description = "Fecha de lanzamiento del videojuego", example = "2017-03-03")
    private final LocalDate fecha_lanzamiento;

    @NotBlank(message = "El genero actualizado no puede estar vacio")
    @Schema(description = "Genero del videojuego", example = "Aventura")
    private final String genero;

    @ValidPlataforma
    @Schema(description = "Plataforma del videojuego", example = "NINTENDO_SWITCH")
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad  actualizada no puede ser negativa.")
    @Schema(description = "Edad recomendada para el videojuego", example = "12")
    private final Integer edad;

}
