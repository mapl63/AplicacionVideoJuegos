package com.example.aplicacionvideojuegos.videoJuegos.mappers;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas unitarias del {@link VideoJuegosMapper}.
 */
@Slf4j
class VideoJuegosMapperTest {

    /**
     * Mapper real usado en los tests.
     */
    private final VideoJuegosMapper videoJuegosMapper = new VideoJuegosMapper();
    /**
     * Cliente auxiliar para relacionar los videojuegos.
     */
    private final Cliente cliente = Cliente.builder()
            .id(1L)
            .nombre("Cliente de Prueba")
            .build();



    @Test
    void toVideoJuegosCreated() {
        log.info("Test del Mapper creando un videojuego");

        // Arrange: DTO con la información necesaria para crear la entidad
        VideoJuegosCreateDto videojuegosCreateDto = VideoJuegosCreateDto.builder()
                .cliente("Cliente de Prueba")
                .nombre("Juego de Prueba")
                .precio(59.99)
                .fecha_lanzamiento(LocalDate.of(2024,1,1))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PC)
                .edad(18)
                .build();

        // Act: convertimos el DTO en entidad
        var resultado = videoJuegosMapper.toVideoJuegosCreated(videojuegosCreateDto,cliente);

        // Assert: cada campo debe coincidir
        assertAll(
                () -> assertEquals(cliente, resultado.getCliente()),
                () -> assertEquals(videojuegosCreateDto.getNombre(), resultado.getNombre()),
                () -> assertEquals(videojuegosCreateDto.getPrecio(), resultado.getPrecio()),
                () -> assertEquals(videojuegosCreateDto.getFecha_lanzamiento(), resultado.getFecha_lanzamiento()),
                () -> assertEquals(videojuegosCreateDto.getGenero(), resultado.getGenero()),
                () -> assertEquals(videojuegosCreateDto.getPlataforma(), resultado.getPlataforma()),
                () -> assertEquals(videojuegosCreateDto.getEdad(), resultado.getEdad())
        );
    }

    @Test
    void toVideoJuegosUpdate() {
        log.info("Test del Mapper actualizando un videojuego");

        // Arrange: DTO con los cambios, entidad actual a actualizar
        VideoJuegosUpdateDto videojuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre("Juego Actualizado")
                .precio(49.99)
                .genero("RPG")
                .plataforma(VideoJuegos.Plataforma.XBOXONE)
                .edad(3)
                .build();

        VideoJuegos videojuegoActualizado = VideoJuegos.builder()
                .id(1L)
                .cliente(cliente)
                .nombre(videojuegosUpdateDto.getNombre())
                .precio(videojuegosUpdateDto.getPrecio())
                .fecha_lanzamiento(videojuegosUpdateDto.getFecha_lanzamiento())
                .genero(videojuegosUpdateDto.getGenero())
                .plataforma(videojuegosUpdateDto.getPlataforma())
                .edad(videojuegosUpdateDto.getEdad())
                .build();

        // Act: aplicamos los cambios sobre la entidad existente
        var resultado = videoJuegosMapper.toVideoJuegosUpdate(videojuegosUpdateDto, videojuegoActualizado);

        // Assert: se actualizan los campos editables y se preserva el resto
        assertAll(
                () -> assertEquals(videojuegoActualizado.getId(), resultado.getId()),
                () -> assertEquals(videojuegoActualizado.getCliente().getNombre(), resultado.getCliente().getNombre()),
                () -> assertEquals(videojuegosUpdateDto.getNombre(), resultado.getNombre()),
                () -> assertEquals(videojuegosUpdateDto.getPrecio(), resultado.getPrecio()),
                () -> assertEquals(videojuegoActualizado.getFecha_lanzamiento(), resultado.getFecha_lanzamiento()),
                () -> assertEquals(videojuegosUpdateDto.getGenero(), resultado.getGenero()),
                () -> assertEquals(videojuegosUpdateDto.getPlataforma(), resultado.getPlataforma()),
                () -> assertEquals(videojuegosUpdateDto.getEdad(), resultado.getEdad())
        );

    }

    @Test
    void toVideoJuegosResponseDto() {
        log.info("Test del Mapper responseDTO de un videojuego");
        // Arrange: entidad completa que debe transformarse a DTO
        VideoJuegos videojuego = VideoJuegos.builder()
                .id(1L)
                .cliente(cliente)
                .nombre("Juego de Prueba")
                .precio(59.99)
                .fecha_lanzamiento(LocalDate.of(2024,1,1))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PC)
                .edad(18)
                .build();

        // Act: convertimos la entidad a DTO de respuesta
        var resultado = videoJuegosMapper.toVideoJuegosResponseDto(videojuego);
        // Assert: cada campo coincide
        assertAll(
                () -> assertEquals(videojuego.getId(), resultado.getId()),
                () -> assertEquals(videojuego.getCliente().getNombre(), resultado.getCliente()),
                () -> assertEquals(videojuego.getNombre(), resultado.getNombre()),
                () -> assertEquals(videojuego.getPrecio(), resultado.getPrecio()),
                () -> assertEquals(videojuego.getFecha_lanzamiento(), resultado.getFecha_lanzamiento()),
                () -> assertEquals(videojuego.getGenero(), resultado.getGenero()),
                () -> assertEquals(videojuego.getPlataforma(), resultado.getPlataforma()),
                () -> assertEquals(videojuego.getEdad(), resultado.getEdad())
        );

    }

}