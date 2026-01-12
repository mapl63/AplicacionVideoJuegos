package com.example.aplicacionvideojuegos.webSockets.notifications.dto;

public record VideoJuegosNotificationResponse (
        Long id,

        String cliente,

        String nombre,

        Double precio,

        String fecha_lanzamiento,

        String genero,

        String plataforma

){ }
