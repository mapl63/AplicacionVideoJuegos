package com.example.aplicacionvideojuegos.clientes.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
public class ClienteRequestDto {

    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private final String nombre;

    private final Boolean isDeleted;

}
