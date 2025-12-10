package com.example.aplicacionvideojuegos.clientes.services;


import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.exceptions.ClienteConflictException;
import com.example.aplicacionvideojuegos.clientes.exceptions.ClienteNotFoundException;
import com.example.aplicacionvideojuegos.clientes.mappers.ClientesMapper;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.repositories.ClienteRepository;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = {"clientes"})
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClientesMapper clientesMapper;

    @Override
    public Page<Cliente> findAll( Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable){
        log.info("Buscando clientes por nombre {}, isDeleted {}", nombre, isDeleted);

        Specification<Cliente> specNombreCliente = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> criterio = Specification.allOf(specNombreCliente, specIsDeleted);
        return clienteRepository.findAll(criterio, pageable);

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
    @CachePut(key = "#result.id")
    public Cliente save(ClienteRequestDto clienteRequestDto) {
        log.info("Guardando cliente {}", clienteRequestDto);

        clienteRepository.findByNombreEqualsIgnoreCase(clienteRequestDto.getNombre())
                .ifPresent(cli -> {
                    throw new ClienteConflictException("Ya existe un cliente con el nombre " + clienteRequestDto.getNombre());
                });
        return clienteRepository.save(clientesMapper.toClienteCreated(clienteRequestDto));

    }

    @Override
    @CachePut(key = "#id")
    public Cliente update(Long id, ClienteRequestDto clienteRequestDto) {
        log.info("Actualizando cliente: {}", clienteRequestDto);
        Cliente clienteActual = findById(id);

        clienteRepository.findByNombreEqualsIgnoreCase(clienteRequestDto.getNombre())
                .ifPresent(cli -> {
                    if (!cli.getId().equals(id)) {
                        throw new ClienteConflictException("Ya existe un cliente con el nombre " + clienteRequestDto.getNombre());
                    }
                });
        return clienteRepository.save(clientesMapper.toClienteUpdated(clienteRequestDto, clienteActual));
    }

    @Override
    @CacheEvict(key = "#id")
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
