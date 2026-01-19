package com.example.aplicacionvideojuegos.rest.clientes.services;

import com.example.aplicacionvideojuegos.rest.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.rest.clientes.exceptions.ClienteConflictException;
import com.example.aplicacionvideojuegos.rest.clientes.mappers.ClientesMapper;
import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.repositories.ClienteRepository;
import com.example.aplicacionvideojuegos.rest.clientes.services.ClienteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {


    private final Cliente cliente = Cliente.builder()
                                            .id(1L)
                                            .nombre("Jose")
                                            .build();


    private final ClienteRequestDto clienteDto = ClienteRequestDto.builder()
                                                                    .nombre("Jose")
                                                                    .build();


    @Mock
    private ClienteRepository clienteRepository;


    @InjectMocks
    private ClienteServiceImpl clienteService;

    @Spy
    private ClientesMapper clientesMapper;

    @Test
    void findAll() {
        log.info("metodo para comprobar que encuentre a todos los clientes");

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(List.of(cliente));

        when(clienteRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        var resultado = clienteService.findAll(Optional.empty(),Optional.empty(), pageable);

        assertAll("findAll",
            () -> assertNotNull(resultado),
            () -> assertFalse(resultado.isEmpty())
        );

        verify(clienteRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));

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

        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        clienteService.save(clienteDto);

        assertAll("save",
            () -> assertNotNull(cliente),
            () -> assertEquals("Jose", cliente.getNombre())
        );

        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testSavedConfict(){

        log.info("metodo para comprobar que no guarde un cliente ya existente");

        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));


        var resultado = assertThrows(ClienteConflictException.class,
                () -> clienteService.save(clienteDto));

        assertAll("saveConflict",
            () -> assertNotNull(resultado),
            () -> assertEquals("Ya existe un cliente con el nombre Jose", resultado.getMessage())
        );


        // Verify: se consulta el repositorio y no se intenta guardar
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(0)).save(any(Cliente.class));
    }

    @Test
    public void testUpdate(){
        log.info("metodo para comprobar que actualice un cliente");


        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);


        var resultado = clienteService.update(1L, clienteDto);


        assertAll("update",
            () -> assertNotNull(resultado),
            () -> assertEquals("Jose", resultado.getNombre())
        );


        verify(clienteRepository, times(1)).findById(anyLong());
        verify(clienteRepository, times(1)).findByNombreEqualsIgnoreCase(anyString());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testUpdateConflict(){
        log.info("metodo para comprobar que no actualice un cliente ya existente");

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByNombreEqualsIgnoreCase(anyString())).thenReturn(Optional.of(cliente));

        var resultado = assertThrows(ClienteConflictException.class,
                () -> clienteService.update(2L, clienteDto));


        assertAll("updateConflict",
            () -> assertNotNull(resultado),
            () -> assertEquals("Ya existe un cliente con el nombre Jose", resultado.getMessage())
        );

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