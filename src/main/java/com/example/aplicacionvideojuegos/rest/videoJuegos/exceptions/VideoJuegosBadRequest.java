package com.example.aplicacionvideojuegos.rest.videoJuegos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VideoJuegosBadRequest extends VideoJuegosException {

    public VideoJuegosBadRequest(String message) {
        super(message);
    }
}
