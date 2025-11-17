package com.example.aplicacionvideojuegos.clientes.mappers;

import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClientesMapper {

    public Cliente toClienteCreated(ClienteRequestDto dto) {
        return Cliente.builder()
                .id(null)
                .nombre(dto.getNombre())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    public Cliente toClienteUpdated(ClienteRequestDto dto, Cliente cliente) {
        return Cliente.builder()
                .id(cliente.getId())
                .nombre(dto.getNombre() != null ? dto.getNombre() : cliente.getNombre())
                .fechaCreacion(cliente.getFechaCreacion())
                .fechaActualizacion(LocalDateTime.now())
                .isDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : cliente.getIsDeleted())
                .build();
    }


}
