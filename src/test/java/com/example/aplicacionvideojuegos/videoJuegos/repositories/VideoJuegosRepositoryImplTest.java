package com.example.aplicacionvideojuegos.videoJuegos.repositories;

import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class VideoJuegosRepositoryImplTest {

    private final VideoJuegos videoJuegos1 = VideoJuegos.builder()
            .id(1L)
            .nombre("GTA VI")
            .precio(120.0)
            .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
            .genero("Acción")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private final VideoJuegos videoJuegos2 = VideoJuegos.builder()
            .id(2L)
            .nombre("The Witcher 4")
            .precio(89.99)
            .fecha_lanzamiento(LocalDate.of(2027, 7, 24))
            .genero("RPG")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private VideoJuegosRepositoryImpl repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new VideoJuegosRepositoryImpl();
        repositorio.save(videoJuegos1);
        repositorio.save(videoJuegos2);
    }

    @Test
    void findAll() {
        log.info("Buscando todos los videojuegos con findAll()");
        List<VideoJuegos> videojuegos = repositorio.findAll();

        assertAll("findAll",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(2, videojuegos.size())
        );
    }

    @Test
    void findAllByNombre() {
        log.info("Buscando videojuegos por nombre con findAllByNombre()");
        String nombre = "GTA VI";

        List<VideoJuegos> videojuegos = repositorio.findAllByNombre(nombre);

        assertAll("findAllByNombre",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(nombre, videojuegos.getFirst().getNombre())
        );
    }

    @Test
    void findAllByGenero() {
        log.info("Buscando videojuegos por género con findAllByGenero()");
        String genero = "Acción";

        List<VideoJuegos> videojuegos = repositorio.findAllByGenero(genero);

        assertAll("findAllByGenero",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(genero, videojuegos.getFirst().getGenero())
        );
    }

    @Test
    void findAllByPlataforma() {
        log.info("Buscando videojuegos por plataforma con findAllByPlataforma()");
        VideoJuegos.Plataforma plataforma = VideoJuegos.Plataforma.PS5;

        List<VideoJuegos> videojuegos = repositorio.findAllByPlataforma(plataforma);

        assertAll("findAllByPlataforma",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(2, videojuegos.size()),
                () -> assertTrue(videojuegos.stream().allMatch(v -> v.getPlataforma() == plataforma))
        );
    }

    @Test
    void findByNombreAndGenero() {
        log.info("Buscando videojuegos por nombre y género con findByNombreAndGenero()");
        String nombre = "The Witcher 4";
        String genero = "RPG";

        List<VideoJuegos> videojuegos = repositorio.findByNombreAndGenero(nombre, genero);

        assertAll("findByNombreAndGenero",
                () -> assertNotNull(videojuegos),
                () -> assertEquals(1, videojuegos.size()),
                () -> assertEquals(nombre, videojuegos.getFirst().getNombre()),
                () -> assertEquals(genero, videojuegos.getFirst().getGenero())
        );
    }

    @Test
    void findByIdConIDValido() {
        log.info("Buscando videojuego por ID con findById() usando un ID válido");
        Long id = 1L;

        Optional<VideoJuegos> optionalJuego = repositorio.findById(id);

        assertAll("findByIdConIDValido",
                () -> assertNotNull(optionalJuego),
                () -> assertTrue(optionalJuego.isPresent()),
                () -> assertEquals(id, optionalJuego.get().getId())
        );
    }

    @Test
    void findByIdConIDInvalido() {
        log.info("Buscando videojuego por ID con findById() usando un ID inválido");
        Long id = 100L;

        Optional<VideoJuegos> optionalJuego = repositorio.findById(id);

        assertAll("findByIdConIDInvalido",
                () -> assertNotNull(optionalJuego),
                () -> assertFalse(optionalJuego.isPresent())
        );
    }

    @Test
    void saveSiNoExiste() {
        log.info("Guardando un nuevo videojuego con save()");
        VideoJuegos nuevoJuego = VideoJuegos.builder()
                .nombre("Minecraft")
                .precio(29.99)
                .fecha_lanzamiento(LocalDate.of(2011, 11, 18))
                .genero("Sandbox")
                .plataforma(VideoJuegos.Plataforma.PC)
                .edad(7)
                .build();

        VideoJuegos guardado = repositorio.save(nuevoJuego);
        var all = repositorio.findAll();

        assertAll("saveSiNoExiste",
                () -> assertNotNull(guardado),
                () -> assertNotNull(guardado.getId()),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void saveSiExiste() {
        log.info("Actualizando un videojuego existente con save()");
        VideoJuegos videoJuegos = VideoJuegos.builder().id(1L).build();


        VideoJuegos savedJuego = repositorio.save(videoJuegos);

        var all = repositorio.findAll();

        assertAll("saveSiExiste",
                () -> assertNotNull(savedJuego),
                () -> assertEquals(videoJuegos, savedJuego),
                () -> assertEquals(2, all.size())
        );
    }

    @Test
    void deleteByIdSiExiste() {
        log.info("Borrando videojuego con deleteById() si existe");
        Long id = 1L;
        repositorio.deleteById(id);
        var all = repositorio.findAll();

        assertAll("deleteById",
                () -> assertEquals(1, all.size()),
                () -> assertTrue(all.stream().noneMatch(v -> v.getId().equals(id)))
        );
    }


    @Test
    void nextId() {
        log.info("Obteniendo siguiente ID con nextId()");
        Long nextId = repositorio.nextId();

        var all = repositorio.findAll();

        assertAll("nextId",
                () -> assertEquals(3L, nextId),
                () -> assertEquals(2,all.size())
        );
    }
}
