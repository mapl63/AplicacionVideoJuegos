package com.example.aplicacionvideojuegos.clientes.services;


import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface ClienteService {

    Page<Cliente> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Cliente findByNombre(String nombre);

    Cliente findById(Long id);

    Cliente save(ClienteRequestDto clienteRequestDto);

    Cliente update(Long id, ClienteRequestDto clienteRequestDto);

    void deleteById(Long id);

}
