package com.example.aplicacionvideojuegos.graphql.controllers;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.repositories.ClienteRepository;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.repositories.VideoJuegosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VideoJuegoClienteGraphQLController {
    private final VideoJuegosRepository videoJuegosRepository;
    private final ClienteRepository clienteRepository;

    // --- QUERIES --- //

    @QueryMapping
    public List<VideoJuegos> videoJuegos() {
        return videoJuegosRepository.findAll();
    }

    @QueryMapping
    public VideoJuegos videoJuegoById(@Argument Long id) {
        Optional<VideoJuegos> videoJuegosOpt = videoJuegosRepository.findById(id);
        return videoJuegosOpt.orElse(null);
    }

    @QueryMapping
    public List<Cliente> clientes() {
        return clienteRepository.findAll();
    }

    @QueryMapping
    public Cliente clienteById(@Argument Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // titularesByNombre(nombre: String!): [Cliente!]!
    @QueryMapping
    public List<Cliente> clientesByNombre(@Argument String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // --- RESOLVERS RELACIONES --- //
    @SchemaMapping(typeName = "VideoJuegos", field = "cliente")
    public Cliente cliente(VideoJuegos videoJuegos) {
        return videoJuegos.getCliente();
    }

    @SchemaMapping(typeName = "Cliente", field = "videoJuegos")
    public List<VideoJuegos> videoJuegos(Cliente cliente) {
        return videoJuegosRepository.findByCliente(cliente);
    }
}
