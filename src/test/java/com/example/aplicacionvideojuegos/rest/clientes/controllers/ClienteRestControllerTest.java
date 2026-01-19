package com.example.aplicacionvideojuegos.rest.clientes.controllers;

import com.example.aplicacionvideojuegos.rest.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.rest.clientes.exceptions.ClienteConflictException;
import com.example.aplicacionvideojuegos.rest.clientes.exceptions.ClienteNotFoundException;
import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.services.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ClienteRestControllerTest {

    private final String ENDPOINT = "/api/v1/clientes";

    private final Cliente cliente1 = Cliente.builder()
            .id(1L)
            .nombre("Marius")
            .build();

    private final Cliente cliente2 = Cliente.builder()
            .id(2L)
            .nombre("Ana")
            .build();

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void getAll() {
        log.info("Devolviendo todos los clientes");

        var clientes = List.of(cliente1, cliente2);

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(clientes);

        when(clienteService.findAll(Optional.empty(),Optional.empty(), pageable))
                .thenReturn(page);

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();


        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath(".content.length()").isEqualTo(clientes.size());
                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Cliente.class).usingRecursiveComparison().isEqualTo(cliente1);
                    assertThat(json).extractingPath("$.content[1]")
                            .convertTo(Cliente.class).usingRecursiveComparison().isEqualTo(cliente2);
                });

        verify(clienteService, times(1)).findAll(Optional.empty(),Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre(){

        // Arrange: el servicio devolverá solo el cliente filtrado por nombre
        var clientes = List.of(cliente2);
        String queryString = "?nombre=" + cliente2.getNombre();
        Optional<String> nombre = Optional.of(cliente2.getNombre());

        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        var page = new PageImpl<>(clientes);

        when(clienteService.findAll(nombre, Optional.empty(), pageable))
                .thenReturn(page);

        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + queryString)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: la respuesta incluye únicamente el cliente esperado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson().satisfies(json -> {
                    assertThat(json).extractingPath("$.content.length()").isEqualTo(clientes.size());

                    assertThat(json).extractingPath("$.content[0]")
                            .convertTo(Cliente.class).usingRecursiveComparison().isEqualTo(cliente2);
                });

        // Verify: el servicio se invoca exactamente una vez con el filtro
        verify(clienteService, times(1)).findAll(nombre, Optional.empty(), pageable);

    }

    @Test
    void getById(){
        log.info("Devolviendo un cliente por ID");

        Long id = cliente1.getId();

        when(clienteService.findById(id)).thenReturn(cliente1);


        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();


        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Cliente.class).usingRecursiveComparison().isEqualTo(cliente1);;


        verify(clienteService, only()).findById(anyLong());
    }

    @Test
    void getById_shouldThrowTitularNotFound__WhenInvalidProvided(){
        log.info("Devolviendo un error al buscar un cleinte por id invalido");

        Long id = 3L;
        when(clienteService.findById(anyLong()))
                .thenThrow(new ClienteNotFoundException(id));


        var resultado = mockMvcTester.get()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();


        assertThat(resultado)
                .hasStatus4xxClientError()
                .hasFailed().failure()
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining(" no encontrado");

        verify(clienteService, only()).findById(anyLong());
    }

    @Test
    void create(){

        String requestBody = """
                {
                    "nombre": "Carlos"
                }
                """;

        var clienteSaved = Cliente.builder()
                .id(1L)
                .nombre("Carlos")
                .build();
        // Arrange: el servicio devolverá el cliente recién guardado
        when(clienteService.save(any(ClienteRequestDto.class))).thenReturn(clienteSaved);

        // Act: enviamos la petición POST con el JSON válido
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: se recibe 201 con el cuerpo esperado
        assertThat(resultado)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(Cliente.class)
                .usingRecursiveComparison()
                .isEqualTo(clienteSaved);

        // Verify: la capa de servicio se invoca exactamente una vez
        verify(clienteService, only()).save(any(ClienteRequestDto.class));
    }

    @Test
    void create_whenBadRequest(){
        log.info("Creando un cliente con datos invalidos");

        String requestBody = """
                {
                    "nombre": null
                }
                """;

        // Act: enviamos un cuerpo inválido en la petición
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: el validador responde 400 con detalles en la clave errores
        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                    assertThat(path).hasFieldOrProperty("nombre"));

        // Verify: nunca se llegó a invocar la lógica de servicio
        verify(clienteService, never()).save(any(ClienteRequestDto.class));
    }

    @Test
    void create_whenNombreExists(){
        log.info("Creando un cliente con un nombre ya existente");
        String requestBody = """
                {
                    "nombre": "Marius"
                }
                """;

        when(clienteService.save(any(ClienteRequestDto.class)))
                .thenThrow(new ClienteConflictException("Ya existe un cliente con el nombre proporcionado"));

        // Act: se intenta crear un recurso con un nombre duplicado
        var resultado = mockMvcTester.post()
                .uri(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: el controlador responde 409 y propaga la excepción de conflicto
        assertThat(resultado)
                .hasStatus(HttpStatus.CONFLICT)
                .hasFailed().failure()
                .isInstanceOf(ClienteConflictException.class)
                .hasMessageContaining("Ya existe un cliente");

        // Verify: se invoca una sola vez al servicio
        verify(clienteService, only()).save(any(ClienteRequestDto.class));
    }

    @Test
    void update(){
        log.info("Creando un cliente por ID");

        Long id = 1L;

        String requestBody = """
                {
                    "nombre": "CARLOS"
                }
                """;

        var clienteSaved = Cliente.builder()
                .id(id)
                .nombre("CARLOS")
                .build();

        // Arrange: el servicio devuelve el cliente actualizado
        when(clienteService.update(anyLong(), any(ClienteRequestDto.class)))
                .thenReturn(clienteSaved);
        
        // Act: se envía la petición PUT con el cuerpo válido
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();
        
        // Assert: la respuesta contiene el cliente actualizado
        assertThat(resultado)
                .hasStatusOk()
                .bodyJson()
                .convertTo(Cliente.class)
                .usingRecursiveComparison()
                .isEqualTo(clienteSaved);
        
        // Verify: solo una invocación al servicio
        verify(clienteService, only()).update(anyLong(), any(ClienteRequestDto.class));
        
    }

    @Test
    void update_shouldThrowTitularNotFound() {
        log.info("Actualizando un cliente para que diga not found");
        // Arrange
        Long id = 3L;
        String requestBody = """
           {
              "nombre": "JOSE"
           }
           """;
        when(clienteService.update(anyLong(), any(ClienteRequestDto.class)))
                .thenThrow(new ClienteNotFoundException(id));
        // Act
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasFailed().failure()
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessageContaining("no encontrado");

        // Verify: el servicio recibió una única llamada
        verify(clienteService, only()).update(anyLong(), any());
    }

    @Test
    void update_shouldThorwBadRequest(){
        log.info("Actualizando un cliente que devuelva bad request");
        Long id = 3L;

        String requestBody = """
                {
                    "nombre": null
                }
                """;

        // Act: se envía la actualización con datos inválidos
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: el validador devuelve 400 y detalla que falta el nombre
        assertThat(resultado)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.errores", path ->
                        assertThat(path).hasFieldOrProperty("nombre"));

        // Verify: la operación de servicio no se ejecuta
        verify(clienteService, never()).update(anyLong(), any(ClienteRequestDto.class));
    }

    @Test
    void update_whenNombreExists(){
        log.info("Actualizando un cliente que ya existe para que salte el conflicto");
        Long id = 1L;
        String requestBody = """
                {
                    "nombre": "Marius"
                }
                """;

        // Arrange: el servicio indica conflicto cuando el nombre ya está usado
        when(clienteService.update(anyLong(), any(ClienteRequestDto.class)))
                .thenThrow(new ClienteConflictException("Ya existe un cliente con el nombre Marius"));

        // Act: intento de actualización con nombre duplicado
        var resultado = mockMvcTester.put()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .exchange();

        // Assert: se recibe un 409 y se propaga la excepción de conflicto
        assertThat(resultado)
                .hasStatus(HttpStatus.CONFLICT)
                .hasFailed().failure()
                .isInstanceOf(ClienteConflictException.class)
                .hasMessageContaining("Ya existe un cliente");

        // Verify: una única llamada al servicio
        verify(clienteService, only()).update(anyLong(), any(ClienteRequestDto.class));
    }

    @Test
    void delete(){
        log.info("Eliminando un cliente por ID");

        Long id = 1L;

        // Arrange: mockeamos que el servicio borra sin errores
        doNothing().when(clienteService).deleteById(anyLong());

        // Act: se envía DELETE al endpoint
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: el controlador responde 204 No Content
        assertThat(resultado)
                .hasStatus(HttpStatus.NO_CONTENT);

        // Verify: la capa de servicio se invoca solo una vez
        verify(clienteService, only()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrowClienteNotFound(){
        log.info("Eliminando un cliente por ID que no existe");

        Long id = 1L;

        // Arrange: el servicio lanza ClienteNotFoundException al borrar
        doThrow(new ClienteNotFoundException(id))
                .when(clienteService).deleteById(anyLong());

        // Act: petición DELETE con id inexistente
        var resultado = mockMvcTester.delete()
                .uri(ENDPOINT + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert: el controlador devuelve 404
        assertThat(resultado)
                .hasStatus(HttpStatus.NOT_FOUND);

        // Verify: una única llamada al servicio
        verify(clienteService, only()).deleteById(anyLong());
    }
}