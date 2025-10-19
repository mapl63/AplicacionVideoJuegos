package com.example.aplicacionvideojuegos.videoJuegos.repositories;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class VideoJuegosRepositoryImpl implements VideoJuegosRepository {
    private final Map<Long, VideoJuegos> videojuegos = new LinkedHashMap<>(
            Map.of(
                    1L, new VideoJuegos(1L, "GTA VI", 120.0, LocalDate.of(2026, 5, 26), "Acción", VideoJuegos.Plataforma.PS5, 18),
                    2L, new VideoJuegos(2L, "The Witcher 4", 89.99, LocalDate.of(2027, 7, 24), "RPG", VideoJuegos.Plataforma.PS5, 18),
                    3L, new VideoJuegos(3L, "Counter-Strike", 39.99, LocalDate.of(2012, 9, 21), "Shooter", VideoJuegos.Plataforma.PC, 16),
                    4L, new VideoJuegos(4L, "Minecraft", 29.99, LocalDate.of(2011, 11, 18), "Sandbox", VideoJuegos.Plataforma.PC, 7),
                    5L, new VideoJuegos(5L, "The Legend of Zelda: Tears of the Kingdom", 59.99, LocalDate.of(2023, 5, 12), "Aventura", VideoJuegos.Plataforma.NINTENDO, 12),
                    6L, new VideoJuegos(6L, "FC 26", 99.99, LocalDate.of(2025, 9, 21), "Deportes", VideoJuegos.Plataforma.PS5, 3),
                    7L, new VideoJuegos(7L, "Call of Duty: Modern Warfare II", 59.99, LocalDate.of(2022, 10, 28), "Shooter", VideoJuegos.Plataforma.XBOXONE, 18)
            )
    );


    @Override
    public List<VideoJuegos> findAll() {
        log.info("Buscando todos los videoJuegos");
        return videojuegos.values()
                .stream()
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
        log.info("Guardando videojuegos: "  +  videojuegos);

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
