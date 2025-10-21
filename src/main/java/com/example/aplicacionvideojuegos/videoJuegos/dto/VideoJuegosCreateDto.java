package com.example.aplicacionvideojuegos.videoJuegos.dto;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoJuegosCreateDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @Positive(message = "El precio no puede ser negativo ni 0")
    private final Double precio;

    @PastOrPresent(message = "La fecha de lanzamiento debe ser anterior a la fecha actual")
    private final LocalDate fecha_lanzamiento;

    @NotNull(message = "El genero no puede ser nulo")
    private final String genero;

    @NotNull(message = "La plataforma no puede ser nula")
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad no puede ser negativa.")
    private final Integer edad;

}
