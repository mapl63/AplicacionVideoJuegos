package com.example.aplicacionvideojuegos.videoJuegos.dto;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoJuegosCreateDto {

    @NotBlank(message = "El nombre no puede estar vacío")
    private final String nombre;

    @Positive(message = "El precio no puede ser negativo")
    private final Double precio;

    @PastOrPresent(message = "La fecha de lanzamiento no puede ser modificada")
    private final LocalDate fecha_lanzamiento;

    @NotNull(message = "El genero no puede ser nulo")
    private final String genero;

    @Pattern(regexp = "^(PS4|PS5|PC|XBOXONE|NINTENDO)$",message = "La plataforma no es válida. Debe ser uno de: PS4, PS5, PC, XBOXONE, NINTENDO")
    private final VideoJuegos.Plataforma plataforma;

    @Positive(message = "La edad no puede ser negativa.")
    private final Integer edad;

}
