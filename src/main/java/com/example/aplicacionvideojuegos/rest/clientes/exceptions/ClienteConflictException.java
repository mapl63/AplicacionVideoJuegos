package com.example.aplicacionvideojuegos.rest.clientes.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ClienteConflictException extends ClienteException {

    public ClienteConflictException(String message) {
        super(message);
    }
}
