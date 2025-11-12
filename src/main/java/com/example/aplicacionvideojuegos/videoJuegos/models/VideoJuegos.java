package com.example.aplicacionvideojuegos.videoJuegos.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class VideoJuegos {

    private Long id;
    private String nombre;
    private Double precio;
    private LocalDate fecha_lanzamiento;
    private String genero;
    private Plataforma  plataforma;
    private Integer edad;

    public enum Plataforma {
        PS4, PS5, PC, XBOXONE, NINTENDO
    }

}
