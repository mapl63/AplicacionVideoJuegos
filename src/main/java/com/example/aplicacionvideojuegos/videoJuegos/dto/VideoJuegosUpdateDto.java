package com.example.aplicacionvideojuegos.videoJuegos.dto;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.Data;
import java.time.LocalDate;

@Data
public class VideoJuegosUpdateDto {

    private final String nombre;
    private final Double precio;
    private final LocalDate fecha_lanzamiento;
    private final String genero;
    private final VideoJuegos.Plataforma plataforma;
    private final Integer edad;

}
