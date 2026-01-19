package com.example.aplicacionvideojuegos.rest.videoJuegos.dto;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "VideoJuegos a devolver como respuesta")
public class VideoJuegosResponseDto {

    @Schema(description = "Identificador del videojuego", example = "1")
    private Long id;

    @Schema(description = "Cliente del videojuego", example = "Cliente1")
    private String cliente;

    @Schema(description = "Nombre del videojuego", example = "Avatar")
    private String nombre;

    @Schema(description = "Precio del videojuego", example = "59.99")
    private Double precio;

    @Schema(description = "Fecha de lanzamiento del videojuego", example = "2023-11-15")
    private LocalDate fecha_lanzamiento;

    @Schema(description = "Género del videojuego", example = "Aventura")
    private String genero;

    @Schema(description = "Plataforma del videojuego", example = "PC")
    private VideoJuegos.Plataforma plataforma;

    @Schema(description = "Edad recomendada para el videojuego", example = "12")
    private Integer edad;

}
