package com.example.aplicacionvideojuegos.videoJuegos.repositories;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
public class VideoJuegosRepositoryImpl implements VideoJuegosRepository {
    private final Map<Long, VideoJuegos> videojuegos = new LinkedHashMap<>(
            Map.of(
                    1L, VideoJuegos.builder()
                            .id(1L)
                            .nombre("GTA VI")
                            .precio(120.0)
                            .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
                            .genero("Acción")
                            .plataforma(VideoJuegos.Plataforma.PS5)
                            .edad(18)
                            .build(),

                    2L, VideoJuegos.builder()
                            .id(2L)
                            .nombre("The Witcher 4")
                            .precio(89.99)
                            .fecha_lanzamiento(LocalDate.of(2027, 7, 24))
                            .genero("RPG")
                            .plataforma(VideoJuegos.Plataforma.PS5)
                            .edad(18)
                            .build()

                    )
    );



    @Override
    public List<VideoJuegos> findAll() {
        log.info("Buscando todos los videoJuegos");
        return videojuegos.values()
                .stream()
                //.sorted(Comparator.comparing(VideoJuegos::getId))
                .toList();
    }

    @Override
    public List<VideoJuegos> findAllByNombre(String nombre) {
        log.info("Buscando videoJuegos por el nombre");
        return videojuegos.values()
                .stream()
                .filter(v ->
                        v.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())).toList();
    }

    @Override
    public List<VideoJuegos> findAllByGenero(String genero) {
        log.info("Buscando videoJuegos por el genero");
        return videojuegos.values().stream()
                .filter(v -> v.getGenero().toLowerCase().contains(genero.trim().toLowerCase())).toList();
    }

    @Override
    public List<VideoJuegos> findAllByPlataforma(VideoJuegos.Plataforma plataforma) {
        log.info("Buscando videoJuegos por la plataforma");
        return videojuegos.values().stream()
                .filter(v -> v.getPlataforma()==plataforma).toList();

    }

    @Override
    public List<VideoJuegos> findByNombreAndGenero(String nombre,String genero) {
        log.info("Buscando videoJuegos por el nombre y por el genero");
        return videojuegos.values().stream()
                .filter(v ->
                        v.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())
                                &&
                                v.getGenero().toLowerCase().contains(genero.trim().toLowerCase())).toList();
    }

    @Override
    public List<VideoJuegos> findByNombreAndPlataforma(String nombre, VideoJuegos.Plataforma plataforma) {
        log.info("Buscando videoJuegos por nombre y plataforma");
        return videojuegos.values().stream()
                .filter(vd ->
                        vd.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())
                        &&
                        vd.getPlataforma() == plataforma).toList();
    }

    @Override
    public List<VideoJuegos> findByGeneroAndPlataforma(String genero, VideoJuegos.Plataforma plataforma) {
        log.info("Buscando videoJuegos por genero y plataforma");
        return videojuegos.values().stream()
                .filter(vd ->
                        vd.getGenero().toLowerCase().contains(genero.trim().toLowerCase())
                        &&
                        vd.getPlataforma() == plataforma).toList();
    }

    @Override
    public Optional<VideoJuegos> findById(Long id) {
        log.info("Buscando videoJuegos por el id");
        return videojuegos.get(id) ==  null ? Optional.empty() :Optional.of(videojuegos.get(id));
    }

    @Override
    public void deleteById(Long id) {
        log.info("Borramos el videojuego por el id: " + id);
        videojuegos.remove(id);
    }

    @Override
    public VideoJuegos save(VideoJuegos videojuego) {
        // Si el ID es null, asignar uno automáticamente
        if (videojuego.getId() == null) {
            // Generamos el siguiente ID
            Long nextId = videojuegos.keySet().stream()
                    .max(Long::compare)
                    .orElse(0L) + 1;
            videojuego.setId(nextId);
        }

        log.info("Guardando videojuego con ID: " + videojuego.getId());

        videojuegos.put(videojuego.getId(), videojuego);
        return videojuego;
    }
    @Override
    public Long nextId() {
        log.debug("Obteniendo siguiente id de VideoJuegos");
        return videojuegos.keySet()
                .stream()
                .mapToLong(vj -> vj)
                .max()
                .orElse(0) + 1;
    }
}
