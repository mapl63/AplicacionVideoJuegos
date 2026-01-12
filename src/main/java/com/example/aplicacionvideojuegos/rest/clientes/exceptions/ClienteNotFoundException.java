package com.example.aplicacionvideojuegos.rest.clientes.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundException extends ClienteException {

    public ClienteNotFoundException(Long id) {
        super("Cliente con id " + id + " no encontrado");
    }

    public ClienteNotFoundException(String cliente) {
        super("Cliente " + cliente + " no encontrado");
    }

}
