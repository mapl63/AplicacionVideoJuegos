package com.example.aplicacionvideojuegos.clientes.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClienteNotFoundException extends ClienteException {

    public ClienteNotFoundException(Long id) {
        super("Cliente con id " + id + " not encontrado");
    }

    public ClienteNotFoundException(String cliente) {
        super("Cliente " + cliente + " no encontrado");
    }

}
