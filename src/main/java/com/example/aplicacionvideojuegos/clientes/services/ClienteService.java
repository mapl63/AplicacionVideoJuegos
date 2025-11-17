package com.example.aplicacionvideojuegos.clientes.services;


import com.example.aplicacionvideojuegos.clientes.dto.ClienteRequestDto;
import com.example.aplicacionvideojuegos.clientes.models.Cliente;

import java.util.*;

public interface ClienteService {

    List<Cliente> findAll(String nombre);

    Cliente findByNombre(String nombre);

    Cliente findById(Long id);

    Cliente save(ClienteRequestDto clienteRequestDto);

    Cliente update(Long id, ClienteRequestDto clienteRequestDto);

    void deleteById(Long id);

}
