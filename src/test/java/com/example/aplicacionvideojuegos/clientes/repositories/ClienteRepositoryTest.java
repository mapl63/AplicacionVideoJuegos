package com.example.aplicacionvideojuegos.clientes.repositories;


import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Slf4j
@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .nombre("Marius")
                .isDeleted(false)
                .build();

        clienteRepository.save(cliente);
    }


    @Test
    void findByNombreEqualsIgnoreCase() {
        log.info("buscamos un cliente por nombre sin importar mayusculas o minusculas");
        Cliente foundCliente = clienteRepository.findByNombreEqualsIgnoreCase("marius").orElse(null);
        log.info("Cliente encontrado: {}", foundCliente);
        assert foundCliente != null;
        assert foundCliente.getNombre().equals("Marius");

    }

    @Test
    void findByNombreEqualsIgnoreCaseAndIsDeletedFalse() {
    }

    @Test
    void findByNombreContainingIgnoreCase() {
    }

    @Test
    void findByNombreContainingIgnoreCaseAndIsDeletedFalse() {
    }

    @Test
    void findByIsDeleted() {
    }

    @Test
    void updateIsDeleteToTrueById() {
    }

    @Test
    void existsVideoJuegoById() {
    }
}