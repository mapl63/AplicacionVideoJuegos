package com.example.aplicacionvideojuegos.webSockets.notifications.dto;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;

import java.time.LocalDate;

public record VideoJuegosNotificationResponse (
        Long id,

        String cliente,

        String nombre,

        Double precio,

        String fecha_lanzamiento,

        String genero,

        String plataforma

){ }
