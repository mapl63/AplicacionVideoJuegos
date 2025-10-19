package com.example.aplicacionvideojuegos.videoJuegos.models;

import lombok.Data;

import java.time.LocalDate;


@Data
public class VideoJuegos {

    public enum Plataforma {
        PS4, PS5, PC, XBOXONE, NINTENDO
    }

    private final Long id;
    private final String nombre;
    private final Double precio;
    private final LocalDate fecha_lanzamiento;
    private final String genero;
    private final Plataforma  plataforma;
    private final Integer edad;

}
