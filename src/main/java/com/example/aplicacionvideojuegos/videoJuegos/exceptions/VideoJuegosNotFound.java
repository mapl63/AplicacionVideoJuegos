package com.example.aplicacionvideojuegos.videoJuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VideoJuegosNotFound extends VideoJuegosException{

    public VideoJuegosNotFound(Long id) {
        super("VideoJuegos con id " + id + " no encontrada");
    }
}
