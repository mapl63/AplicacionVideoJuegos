package com.example.aplicacionvideojuegos.rest.videoJuegos.validators;


import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.EnumSet;

public class PlataformaValidator implements ConstraintValidator<ValidPlataforma, VideoJuegos.Plataforma> {

    @Override
    public boolean isValid(VideoJuegos.Plataforma plataforma, ConstraintValidatorContext context) {
        if (plataforma == null) {
            // Puedes decidir si null es válido o no.
            // Si quieres que null sea inválido, devuelve false.
            return false;
        }

        // Verificamos que el valor esté dentro del conjunto de valores definidos en el enum
        return EnumSet.allOf(VideoJuegos.Plataforma.class).contains(plataforma);
    }
}