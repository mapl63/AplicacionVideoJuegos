package com.example.aplicacionvideojuegos.rest.clientes.repositories;


import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.repositories.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class ClienteRepositoryTest {


    private final Cliente cliente = Cliente.builder().nombre("Marius").build();

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TestEntityManager entityManager;


    @BeforeEach
    void setUp() {
        // Arrange: persistimos un cliente para que exista en la BD de pruebas
        entityManager.persist(cliente);
        entityManager.flush();
    }

    @Test
    void findAll() {
        log.info("buscamos todos los clientes");

        // Act: consultamos todos los registros
        List<Cliente> clientes = clienteRepository.findAll();

        // Assert: la lista no es nula ni vacía
        assertAll("findAll",
                () -> assertNotNull(clientes),
                () -> assertFalse(clientes.isEmpty())
        );
    }



    @Test
    void findByNombre() {
        log.info("buscamos por nombre de cliente");
        // Act: filtramos por nombre parcial
        List<Cliente> clientes = clienteRepository.findByNombreContainingIgnoreCase("Marius");

        // Assert: existe al menos un resultado y coincide el nombre
        assertAll("findByNombre",
                () -> assertNotNull(clientes),
                () -> assertFalse(clientes.isEmpty()),
                () -> assertEquals("Marius", clientes.getFirst().getNombre())
        );

    }

    @Test
    void findById() {
        log.info("Buscando un cliente por id: ");
        // Act: buscamos el cliente con ID 1
        Cliente cliente = clienteRepository.findById(1L).orElse(null);

        // Assert: el cliente existe y mantiene el nombre esperado
        assertAll("findById",
                ()-> assertNotNull(cliente),
                () -> assertEquals("Marius", cliente.getNombre())
                );
    }


    @Test
    void findByIdNotFound(){
        log.info("Metodo para buscar un cliente que no existe");
        // Act: obtenemos un ID que no está en la BD
        Cliente cliente = clienteRepository.findById(100L).orElse(null);
        // Assert: no se devuelve registro
        assertNull(cliente);

    }

    @Test
    void save() {
        log.info("Guardando un nuevo cliente");
        // Act: persistimos un nuevo cliente
        Cliente cliente = clienteRepository.save(Cliente.builder().nombre("Pedro").build());

        // Assert: se genera un registro con el nombre esperado
        assertAll("save",
                () -> assertNotNull(cliente),
                () -> assertEquals("Pedro", cliente.getNombre())
        );
    }

    @Test
    void update(){
        log.info("Actualizando un cliente que ya existe");

        // Arrange: obtenemos el cliente existente
        var clienteExistente = clienteRepository.findById(1L).orElse(null);

        // Act: guardamos la entidad con el nuevo nombre
        Cliente clienteActualizar = Cliente.builder()
                .id(clienteExistente.getId())
                .nombre("Pedro").build();

        Cliente clienteActualizado = clienteRepository.save(clienteActualizar);

        // Assert: el nombre se actualiza manteniendo el registro
        assertAll("update",
                () -> assertNotNull(clienteExistente),
                () -> assertEquals(clienteActualizar.getNombre(), clienteActualizado.getNombre())
        );
    }

    @Test
    void delete(){
        log.info("Eliminando un cliente que ya existe");

        // Arrange: recuperamos el cliente a eliminar
        var clienteBorrar = clienteRepository.findById(1L).orElse(null);

        // Act: se elimina y luego se vuelve a consultar para comprobarlo
        clienteRepository.delete(clienteBorrar);

        Cliente clienteBorrado = clienteRepository.findById(1L).orElse(null);

        // Assert: el registro ya no existe
        assertNull(clienteBorrado);

    }
}