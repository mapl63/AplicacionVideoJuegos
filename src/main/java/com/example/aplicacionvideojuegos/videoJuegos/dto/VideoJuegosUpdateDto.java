package com.example.aplicacionvideojuegos.videoJuegos.dto;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.validators.ValidPlataforma;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class VideoJuegosUpdateDto {

    @NotBlank(message = "El nuevo nombre no puede estar vacío")
    private final String nombre;

    @Positive(message = "El nuevo precio no puede ser negativo")
    private final Double precio;

    private final LocalDate fecha_lanzamiento;

    @NotBlank(message = "El genero actualizado no puede estar vacio")
    private final String genero;

    @ValidPlataforma
    private final VideoJuegos.Plataforma plataforma;

    @PositiveOrZero(message = "La edad  actualizada no puede ser negativa.")
    private final Integer edad;

}
