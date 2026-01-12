package com.example.aplicacionvideojuegos.rest.videoJuegos.dto;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoJuegosResponseDto {

    private Long id;

    private String cliente;

    private String nombre;

    private Double precio;

    private LocalDate fecha_lanzamiento;

    private String genero;

    private VideoJuegos.Plataforma plataforma;

    private Integer edad;

}
