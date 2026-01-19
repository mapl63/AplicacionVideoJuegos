package com.example.aplicacionvideojuegos.rest.videoJuegos.repositories;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;

import com.example.aplicacionvideojuegos.rest.videoJuegos.repositories.VideoJuegosRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Sql(value = {"/reset.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class VideoJuegosRepositoryTest {

    private final Cliente cliente1  = Cliente.builder()
            .nombre("Juan")
            .build();

    private final Cliente cliente2  = Cliente.builder()
            .nombre("Maria")
            .build();

    private final VideoJuegos videoJuegos = VideoJuegos.builder()
            .cliente(cliente1)
            .nombre("Red Dead Redemption 2")
            .precio(59.99)
            .fecha_lanzamiento(java.time.LocalDate.of(2018, 10, 26))
            .genero("Acción")
            .plataforma(VideoJuegos.Plataforma.PS4)
            .edad(18)
            .build();

    private final VideoJuegos videoJuegos2 = VideoJuegos.builder()
            .cliente(cliente2)
            .nombre("The Legend of Zelda: Breath of the Wild")
            .precio(89.99)
            .fecha_lanzamiento(java.time.LocalDate.of(2017, 3, 3))
            .genero("RPG")
            .plataforma(VideoJuegos.Plataforma.NINTENDO)
            .edad(12)
            .build();

    @Autowired
    private VideoJuegosRepository videoJuegosRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {

        entityManager.persist(cliente1);
        entityManager.persist(cliente2);

        entityManager.persist(videoJuegos);
        entityManager.persist(videoJuegos2);

        entityManager.flush();

    }

    @Test
    void findAll() {
        log.info("Buscando todos los videojuegos en la base de datos");
        List<VideoJuegos> videoJuegos = videoJuegosRepository.findAll();

        assertAll("findAll",
                () -> assertNotNull(videoJuegos),
                () -> assertEquals(2, videoJuegos.size())
        );
    }


    @Test
    void findById_existingId_returnsOptionalWithVideoJuego() {
        log.info("Buscando un videojuego por id en la base de datos");

        Long id = 1L;

        Optional<VideoJuegos> optionalVideoJuegos = videoJuegosRepository.findById(id);

        assertAll("findById_existingId_returnsOptionalWithVideoJuego",
                () -> assertNotNull(optionalVideoJuegos),
                () -> assertTrue(optionalVideoJuegos.isPresent()),
                () -> assertEquals(id, optionalVideoJuegos.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional(){
        log.info("Comprobando si no existe un videojuego por id en la base de datos");
        Long id = 9L;
        Optional<VideoJuegos> optinalJuego = videoJuegosRepository.findById(id);

        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optinalJuego),
                () -> assertTrue(optinalJuego.isEmpty())
        );
    }

    @Test
    void existsById_existingId(){
        log.info("Comprobando si existe un videojuego por id en la base de datos");

        Long id = 1L;

        boolean exists = videoJuegosRepository.existsById(id);

        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId(){
        log.info("Comprobando si no existe un videojuego por id en la base de datos");

        Long id = 9L;

        boolean exists = videoJuegosRepository.existsById(id);

        assertFalse(exists);
    }


    @Test
    void save_notExists(){
        log.info("Guardando un nuevo videoJuego si no existe en la base de datos");

        VideoJuegos nuevoVideoJuego = VideoJuegos.builder()
                .cliente(cliente1)
                .nombre("Call of Duty: Modern Warfare")
                .precio(119.99)
                .fecha_lanzamiento(java.time.LocalDate.of(2014, 2, 23))
                .genero("SHOOTER")
                .plataforma(VideoJuegos.Plataforma.XBOXONE)
                .edad(16)
                .build();

        VideoJuegos savedVideoJuego = videoJuegosRepository.save(nuevoVideoJuego);

        var all = videoJuegosRepository.findAll();

        assertAll("save_notExists",
                () -> assertNotNull(savedVideoJuego),
                () -> assertEquals(nuevoVideoJuego, savedVideoJuego),
                () -> assertEquals(3, all.size())
        );
    }

    @Test
    void save_butExists(){
        log.info("Actualizando un videoJuego si existe en la base de datos");

        Long id = 1L;

        VideoJuegos videoJuegoExistente = VideoJuegos.builder()
                .id(id)
                .cliente(cliente1)
                .nombre("FC 26")
                .precio(99.99)
                .fecha_lanzamiento(java.time.LocalDate.of(2018, 10, 26))
                .genero("DEPORTES")
                .plataforma(VideoJuegos.Plataforma.PC)
                .edad(6)
                .build();

        VideoJuegos savedVideoJuego = videoJuegosRepository.save(videoJuegoExistente);

        var all = videoJuegosRepository.findAll();

        assertAll("save_butExists",
                () -> assertNotNull(savedVideoJuego),
                () -> assertTrue(videoJuegosRepository.existsById(id)),
                () -> assertTrue(all.size() >= 2)
        );
    }

    @Test
    void deletedById_existingId() {
        log.info("Eliminando un videojuego por id en la base de datos");

        Long id = 1L;

        videoJuegosRepository.deleteById(id);

        var all = videoJuegosRepository.findAll();

        assertAll("deletedById_existingId",
                () -> assertEquals(1, all.size()),
                () -> assertFalse(videoJuegosRepository.existsById(id))
        );
    }

}