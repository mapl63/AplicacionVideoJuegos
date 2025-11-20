package com.example.aplicacionvideojuegos.clientes.services;

import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.exceptions.ClienteConflictException;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.repositories.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para {@link ClienteServiceImpl}.
 * Se apoya en Mockito para aislar el repositorio y comprobar
 * el comportamiento del servicio sin tocar la base de datos.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    /**
     * Cliente modelo reutilizado en la mayoría de tests.
     */
    private final Cliente cliente = Cliente.builder()
                                            .id(1L)
                                            .nombre("Jose")
                                            .build();

    /**
     * DTO que simula la petición de creación/actualización.
     */
    private final ClienteRequestDto clienteDto = ClienteRequestDto.builder()
                                                                    .nombre("Jose")
                                                                    .build();

    /**
     * Repositorio simulado con Mockito; evita acceder a datos reales.
     */
    @Mock
    private ClienteRepository clienteRepository;

    /**
     * Servicio bajo prueba. Mockito inyecta los mocks declarados arriba.
     */
    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Test
    void findAll() {
        log.info("metodo para comprobar que encuentre a todos los clientes");

        // Arrange: simulamos que el repositorio devuelve un cliente
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        // Act: invocamos al servicio
        var resultado = clienteService.findAll(null);

        // Assert: validamos que la lista no sea nula ni vacía
        assertAll("findAll",
            () -> assertNotNull(resultado),
            () -> assertFalse(resultado.isEmpty())
        );

        // Verify: nos aseguramos de que se llamó al repositorio solo una vez
        verify(clienteRepository, times(1)).findAll();

    }

    @Test
    public void testFindByNombre(){
        log.info("metodo para comprobar que encuentre un cliente por nombre");

        // Arrange: el repositorio devuelve el cliente buscado
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(java.util.Optional.of(cliente));

        // Act: se llama al servicio con el nombre
        var resultado = clienteService.findByNombre("Jose");

        // Assert: comprueba que el cliente existe y tiene el nombre esperado
        assertAll("findByNombre",
            () -> assertNotNull(resultado),
            () -> assertEquals("Jose", resultado.getNombre())
        );

        // Verify: el repositorio debe consultarse solo una vez por nombre
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
    }

    @Test
    public void testFindById(){
        log.info("metodo para comprobar que encuentre un cliente por id");

        // Arrange: simulamos que existe un cliente con ese ID
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));

        // Act: buscamos por id
        var resultado = clienteService.findById(1L);

        // Assert: validamos el ID del cliente obtenido
        assertAll("findById",
            () -> assertNotNull(resultado),
            () -> assertEquals(1L, resultado.getId())
        );

        // Verify: solo se llama una vez al repositorio
        verify(clienteRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSave(){
        log.info("metodo para comprobar que guarde un cliente");

        // Arrange: el nombre no existe y el repositorio guardará el cliente
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act: guardamos un cliente desde el servicio
        clienteService.save(clienteDto);

        // Assert: reutilizamos el objeto local para asegurarnos de que contiene datos
        assertAll("save",
            () -> assertNotNull(cliente),
            () -> assertEquals("Jose", cliente.getNombre())
        );

        // Verify: comprobamos que se validó el nombre y luego se guardó
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testSavedConfict(){

        log.info("metodo para comprobar que no guarde un cliente ya existente");

        // Arrange: el repositorio devuelve un cliente existente para ese nombre
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));

        // Act + Assert: se espera una excepción de conflicto
        var resultado = assertThrows(ClienteConflictException.class,
                () -> clienteService.save(clienteDto));

        // Assert: validamos el mensaje de error
        assertAll("saveConflict",
            () -> assertNotNull(resultado),
            () -> assertEquals("Ya existe un cliente con el nombre: Jose", resultado.getMessage())
        );


        // Verify: se consulta el repositorio y no se intenta guardar
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(0)).save(any(Cliente.class));
    }

    @Test
    public void testUpdate(){
        log.info("metodo para comprobar que actualice un cliente");

        // Arrange: existe el cliente y el nombre no genera conflicto
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act: se actualiza el cliente
        var resultado = clienteService.update(1L, clienteDto);

        // Assert: el resultado no es nulo y mantiene el nombre esperado
        assertAll("update",
            () -> assertNotNull(resultado),
            () -> assertEquals("Jose", resultado.getNombre())
        );

        // Verify: se consulta por ID, por nombre y finalmente se guarda
        verify(clienteRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testUpdateConflict(){
        log.info("metodo para comprobar que no actualice un cliente ya existente");


        // Arrange: el repositorio indica que ya existe otro cliente con ese nombre
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));



        // Act + Assert: la actualización debe lanzar conflicto
        var resultado = assertThrows(ClienteConflictException.class,
                () -> clienteService.update(2L, clienteDto));

        // Assert: se comprueba el mensaje de error
        assertAll("updateConflict",
            () -> assertNotNull(resultado),
            () -> assertEquals("Ya existe un cliente con el nombre: Jose", resultado.getMessage())
        );

        // Verify: no se llega a guardar cuando hay conflicto
        verify(clienteRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(0)).save(any(Cliente.class));
    }

    @Test
    public void testDeleteById(){
        log.info("metodo para comprobar que elimine un cliente por id");

        // Arrange: existe el cliente y no tiene videojuegos asociados
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(clienteRepository.existsVideoJuegoById(anyLong())).thenReturn(false);

        // Act: se elimina por ID
        clienteService.deleteById(1L);

        // Assert: verificamos el objeto reutilizado (no cambia)
        assertAll("deleteById",
            () -> assertNotNull(cliente),
            () -> assertEquals("Jose", cliente.getNombre())
        );

        // Verify: se consulta, se valida que no tenga dependencias y se borra
        verify(clienteRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(1)).existsVideoJuegoById(anyLong());
        verify(clienteRepository, times(1)).deleteById(anyLong());
    }
}