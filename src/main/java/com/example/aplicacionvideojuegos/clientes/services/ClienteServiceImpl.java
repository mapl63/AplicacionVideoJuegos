package com.example.aplicacionvideojuegos.clientes.services;


import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.exceptions.ClienteConflictException;
import com.example.aplicacionvideojuegos.clientes.exceptions.ClienteNotFoundException;
import com.example.aplicacionvideojuegos.clientes.mappers.ClientesMapper;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.repositories.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"clientes"})
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClientesMapper clientesMapper;

    @Override
    public List<Cliente> findAll(String nombre) {
        log.info("Buscando clientes por nombre {}", nombre);
        if (nombre == null || nombre.isEmpty()) {
            return clienteRepository.findAll();
        } else {
            return clienteRepository.findByNombreContainingIgnoreCase(nombre);
        }
    }

    @Override
    public Cliente findByNombre(String nombre) {
        log.info("Buscando cliente por nombre {}", nombre);
        return clienteRepository.findByNombreEqualsIgnoreCase(nombre)
                .orElseThrow(() -> new ClienteNotFoundException(nombre));

    }

    @Override
    @Cacheable
    public Cliente findById(Long id) {
        log.info("Buscando cliente por id {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @CachePut
    public Cliente save(ClienteRequestDto clienteRequestDto) {
        log.info("Guardando cliente {}", clienteRequestDto);
        return clienteRepository
                .save(clientesMapper
                        .toClienteCreated(clienteRequestDto));
    }

    @Override
    @CachePut
    public Cliente update(Long id, ClienteRequestDto clienteRequestDto) {
        log.info("Actualizando cliente: {}", clienteRequestDto);
        Cliente clienteActual = findById(id);

        return clienteRepository
                .save(clientesMapper
                        .toClienteUpdated(clienteRequestDto, clienteActual));
    }

    @Override
    @CacheEvict
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando cliente por id {}", id);
        Cliente cliente = findById(id);

        if (clienteRepository.existsVideoJuegoById(id)) {
            String mensaje = "No se puede borrar el cliente con id " + id + " porque tiene videoJuegos asociadas";
            log.warn(mensaje);
            throw new ClienteConflictException(mensaje);
        } else {
            clienteRepository.deleteById(id);
        }

    }

}
