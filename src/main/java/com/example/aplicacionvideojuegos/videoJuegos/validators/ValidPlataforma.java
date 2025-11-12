package com.example.aplicacionvideojuegos.videoJuegos.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlataformaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlataforma {
    String message() default "La plataforma no es válida. Debe ser PS4, PS5, PC, XBOXONE o NINTENDO.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}