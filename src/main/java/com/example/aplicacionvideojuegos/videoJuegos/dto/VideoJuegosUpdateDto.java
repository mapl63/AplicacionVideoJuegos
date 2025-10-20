package com.example.aplicacionvideojuegos.videoJuegos.dto;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VideoJuegosUpdateDto {

    @NotBlank(message = "El nuevo nombre no puede estar vacío")
    private final String nombre;

    @Positive(message = "El nuevo precio no puede ser negativo")
    private final Double precio;

    @PastOrPresent(message = "La fecha de actualización de lanzamiento no puede ser Actualizada")
    private final LocalDate fecha_lanzamiento;

    @NotNull(message = "El genero actualizado no puede ser nulo")
    private final String genero;

    @NotNull(message = "La plataforma actualizada no puede ser nula")
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad  actualizada no puede ser negativa.")
    private final Integer edad;

}
