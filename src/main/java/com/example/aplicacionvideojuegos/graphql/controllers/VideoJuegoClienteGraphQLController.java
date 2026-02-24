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

/*
========================================================
GRAPHQL - CRUD VideoJuegos y Cliente
========================================================

Este controlador implementa las QUERIES GraphQL
para VideoJuegos y Cliente.

📂 Ubicación resolvers:
src/main/java/com/example/aplicacionvideojuegos/graphql/controllers/

📂 Ubicación esquema GraphQL:
src/main/resources/graphql/schema.graphqls

El schema define:
- types (VideoJuegos, Cliente, enum Plataforma)
- type Query (operaciones de lectura)

Este controlador implementa:
- @QueryMapping → consultas
- @SchemaMapping → relaciones entre tipos

Si en examen piden GraphQL:
1️⃣ Editar schema.graphqls
2️⃣ Crear/editar controller en graphql/controllers
========================================================
*/


@Controller
@RequiredArgsConstructor // genera automáticamente el constructor para inyectar los atributos final (inyección por constructor en Spring).
public class VideoJuegoClienteGraphQLController {

    private final VideoJuegosRepository videoJuegosRepository;
    private final ClienteRepository clienteRepository;

    // ========================================================
    // ======================= QUERIES =========================
    // ========================================================

    /**
     * Query: videoJuegos
     * Devuelve la lista completa de todos los videojuegos.
     * Equivalente REST: GET /videojuegos
     */
    @QueryMapping
    public List<VideoJuegos> videoJuegos() {
        return videoJuegosRepository.findAll();
    }

    /**
     * Query: videoJuegoById(id: ID!)
     * Devuelve un videojuego concreto por su ID.
     * Si no existe devuelve null.
     * Equivalente REST: GET /videojuegos/{id}
     */
    @QueryMapping
    public VideoJuegos videoJuegoById(@Argument Long id) {
        return videoJuegosRepository.findById(id).orElse(null);
    }

    /**
     * Query: clientes
     * Devuelve la lista completa de todos los clientes.
     * Equivalente REST: GET /clientes
     */
    @QueryMapping
    public List<Cliente> clientes() {
        return clienteRepository.findAll();
    }

    /**
     * Query: clienteById(id: ID!)
     * Devuelve un cliente por su ID.
     * Si no existe devuelve null.
     * Equivalente REST: GET /clientes/{id}
     */
    @QueryMapping
    public Cliente clienteById(@Argument Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    /**
     * Query: clienteByNombre(nombre: String!)
     * Devuelve lista de clientes cuyo nombre contenga el texto indicado.
     * Búsqueda case insensitive.
     * Equivalente REST: GET /clientes?nombre=xxx
     */
    @QueryMapping
    public List<Cliente> clientesByNombre(@Argument String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ========================================================
    // =============== RESOLVERS RELACIONES ===================
    // ========================================================

    /**
     * Resuelve la relación VideoJuegos → Cliente
     * Cuando en una query se pide:
     * videoJuegos { cliente { ... } }
     */
    @SchemaMapping(typeName = "VideoJuegos", field = "cliente")
    public Cliente cliente(VideoJuegos videoJuegos) {
        return videoJuegos.getCliente();
    }

    /**
     * Resuelve la relación Cliente → VideoJuegos
     * Cuando en una query se pide:
     * clientes { videoJuegos { ... } }
     */
    @SchemaMapping(typeName = "Cliente", field = "videoJuegos")
    public List<VideoJuegos> videoJuegos(Cliente cliente) {
        return videoJuegosRepository.findByCliente(cliente);
    }
}