package com.example.aplicacionvideojuegos.clientes.mappers;

import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del {@link ClientesMapper}.
 * Se verifican las conversiones entre DTOs y entidades.
 */
@Slf4j
class ClientesMapperTest {

    /**
     * Cliente base para validar los mapeos de actualización.
     */
    private final Cliente cliente = Cliente.builder()
            .id(1L)
            .nombre("Marius")
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .isDeleted(false)
            .build();

    /**
     * Mapper real (sin mocks) usado en los tests.
     */
    private final ClientesMapper clientesMapper = new ClientesMapper();

    /**
     * DTO de entrada con datos válidos.
     */
    private final ClienteRequestDto clienteRequestDto = ClienteRequestDto.builder()
            .nombre("Marius")
            .isDeleted(false)
            .build();


    @Test
    public void whenToCliente_thenReturnCliente() {
        log.info("Cuando se solicita un cliente, entonces debe devolver el cliente esperado.");

        // Act: convertimos el DTO a entidad
        Cliente clienteCreado = clientesMapper.toClienteCreated(clienteRequestDto);

        // Assert: se copian correctamente los campos
        assertAll("whenToCliente_thenReturnCliente",
                () -> assertEquals(clienteRequestDto.getNombre(), clienteCreado.getNombre(), "El nombre no coincide")
        );

    }

    @Test
    void whenToClienteWithExistingCliente_thenReturnUpdatedCliente(){
        log.info("Buscando un cliente existente para actualizarlo y devolver los datos actualizados.");

        // Act: aplicamos el DTO sobre el cliente existente
        Cliente clienteActualizado = clientesMapper.toClienteUpdated(clienteRequestDto, cliente);

        // Assert: los campos cambian según el DTO
        assertAll("whenToClienteWithExistingCliente_thenReturnUpdatedCliente",
                () -> assertEquals(clienteRequestDto.getNombre(), clienteActualizado.getNombre())
        );

    }

    @Test
    public void whenToTitularWithExistingCliente_thenReturnclienteActualizar() {
        log.info("Actualizando un cliente que ya existe={}", clienteRequestDto);

        // Act: actualizamos la entidad existente
        Cliente clienteActualizar = clientesMapper.toClienteUpdated(clienteRequestDto, cliente);
        
        // Assert: se mantienen los campos que no deben variar
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
        // Arrange: preparamos un DTO con un nombre distinto
        ClienteRequestDto viejoCliente = ClienteRequestDto.builder()
                .nombre("Juan")
                .isDeleted(false)
                .build();

        // Act: aplicamos la actualización
        Cliente clienteActualizado = clientesMapper.toClienteUpdated(viejoCliente, cliente);

        // Assert: solo cambia el nombre y la fecha de actualización
        assertAll("Actualizando nombre de cliente con ID",
                () -> assertEquals(cliente.getId(), clienteActualizado.getId(), "El ID no debe cambiar"),
                () -> assertEquals(viejoCliente.getNombre(), clienteActualizado.getNombre(), "El nombre debe ser actualizado"),
                () -> assertEquals(cliente.getFechaCreacion(), clienteActualizado.getFechaCreacion(), "La fecha de creación no debe cambiar"),
                () -> assertTrue(clienteActualizado.getFechaActualizacion().isAfter(cliente.getFechaActualizacion()), "La fecha de actualización debe ser posterior a la anterior"),
                () -> assertEquals(cliente.getIsDeleted(), clienteActualizado.getIsDeleted(), "isDeleted no debe cambiar")
        );
    }

}