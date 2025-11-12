package com.example.aplicacionvideojuegos.videoJuegos.dto;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.validators.ValidPlataforma;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class VideoJuegosCreateDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @Positive(message = "El precio no puede ser negativo ni 0")
    private final Double precio;

    private final LocalDate fecha_lanzamiento;

    @NotBlank(message = "El genero no puede estar vacío")
    private final String genero;

    @ValidPlataforma
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad no puede ser negativa.")
    private final Integer edad;

}
