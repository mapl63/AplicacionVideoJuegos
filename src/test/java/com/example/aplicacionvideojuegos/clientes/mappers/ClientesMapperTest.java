package com.example.aplicacionvideojuegos.clientes.mappers;

import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ClientesMapperTest {

    private final Cliente cliente = Cliente.builder()
            .id(1L)
            .nombre("Marius")
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .isDeleted(false)
            .build();

    private final ClientesMapper clientesMapper = new ClientesMapper();

    private final ClienteRequestDto clienteRequestDto = ClienteRequestDto.builder()
            .nombre("Marius")
            .isDeleted(false)
            .build();


    @Test
    public void whenToCliente_NoExisteCreamosUnTitular() {
        log.info("Creando un nuevo cliente a partir del DTO: {}", clienteRequestDto);
        Cliente clienteNuevo = clientesMapper.toClienteCreated(clienteRequestDto);

        assertAll("whenToCliente_thenReturnCliente",
                () -> assertNull(clienteNuevo.getId(), "El ID debe ser nulo"),
                () -> assertEquals(clienteRequestDto.getNombre(), clienteNuevo.getNombre(), "El nombre no coincide"),
                () -> assertNotNull(clienteNuevo.getFechaCreacion(), "La fecha de creación no debe ser nula"),
                () -> assertNotNull(clienteNuevo.getFechaActualizacion(), "La fecha de actualización no debe ser nula"),
                () -> assertFalse(clienteNuevo.getIsDeleted(), "isDeleted debe ser false")
        );
    }

    @Test
    public void whenToTitularWithExistingCliente_thenReturnclienteActualizar() {
        log.info("Actualizando un cliente que ya existe={}", clienteRequestDto);
        Cliente clienteActualizar = clientesMapper.toClienteUpdated(clienteRequestDto, cliente);
        
        assertAll("whenToTitularWithExistingCliente_thenReturnclienteActualizar",
                () -> assertEquals(cliente.getId(), clienteActualizar.getId(), "El ID no debe cambiar"),
                () -> assertEquals(clienteRequestDto.getNombre(), clienteActualizar.getNombre(), "El nombre no coincide"),
                () -> assertEquals(cliente.getFechaCreacion(), clienteActualizar.getFechaCreacion(), "La fecha de creación no debe cambiar"),
                () -> assertTrue(clienteActualizar.getFechaActualizacion().isAfter(cliente.getFechaActualizacion()), "La fecha de actualización debe ser posterior a la anterior"),
                () -> assertEquals(cliente.getIsDeleted(), clienteActualizar.getIsDeleted(), "isDeleted no debe cambiar")
        );

    }

    @Test
    void cambiandoNombredeClienteConID(){
        ClienteRequestDto viejoCliente = ClienteRequestDto.builder()
                .nombre("Juan")
                .isDeleted(false)
                .build();

        Cliente clienteActualizado = clientesMapper.toClienteUpdated(viejoCliente, cliente);

        assertAll("Actualizando nombre de cliente con ID",
                () -> assertEquals(cliente.getId(), clienteActualizado.getId(), "El ID no debe cambiar"),
                () -> assertEquals(viejoCliente.getNombre(), clienteActualizado.getNombre(), "El nombre debe ser actualizado"),
                () -> assertEquals(cliente.getFechaCreacion(), clienteActualizado.getFechaCreacion(), "La fecha de creación no debe cambiar"),
                () -> assertTrue(clienteActualizado.getFechaActualizacion().isAfter(cliente.getFechaActualizacion()), "La fecha de actualización debe ser posterior a la anterior"),
                () -> assertEquals(cliente.getIsDeleted(), clienteActualizado.getIsDeleted(), "isDeleted no debe cambiar")
        );
    }

}