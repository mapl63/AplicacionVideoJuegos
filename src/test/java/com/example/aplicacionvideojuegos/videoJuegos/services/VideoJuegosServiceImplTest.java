package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.services.ClienteService;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.repositories.VideoJuegosRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class VideoJuegosServiceImplTest {

    private final Cliente cliente1 = Cliente.builder()
            .nombre("Juan")
            .build();
    private final Cliente cliente2 = Cliente.builder()
            .nombre("Maria")
            .build();

    private final Cliente cliente3 = Cliente.builder()
            .nombre("Pedro")
            .build();

    private final VideoJuegos videoJuegos1 = VideoJuegos.builder()
            .id(1L)
            .cliente(cliente1)
            .nombre("GTA VI")
            .precio(120.0)
            .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
            .genero("Acción")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private final VideoJuegos videoJuegos2 = VideoJuegos.builder()
            .id(2L)
            .cliente(cliente2)
            .nombre("The Witcher 4")
            .precio(89.99)
            .fecha_lanzamiento(LocalDate.of(2027, 7, 24))
            .genero("RPG")
            .plataforma(VideoJuegos.Plataforma.PS5)
            .edad(18)
            .build();

    private VideoJuegosResponseDto videoJuegosResponse1;

    @Mock
    private VideoJuegosRepository juegosRepository;

    @Mock
    private ClienteService clienteService;

    @Spy
    private VideoJuegosMapper videoJuegosMapper;

    @InjectMocks
    private VideoJuegosServiceImpl juegosService;

    @Captor
    private ArgumentCaptor<VideoJuegos> videoJuegosCaptor;

    @BeforeEach
    void setUp() {
        videoJuegosResponse1 = videoJuegosMapper.toVideoJuegosResponseDto(videoJuegos1);
    }

    @Test
    void findAll_devolverTodasLasTarjetas_NoPasarNingunParametro() {
        log.info("devolver Todas Las Tarjetas sin pasar ningun parametro");
        List <VideoJuegos> listaVideojuegos = Arrays.asList(videoJuegos1, videoJuegos2);

        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findAll()).thenReturn(listaVideojuegos);

        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(null,null);

        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        verify(juegosRepository, times(1)).findAll();

    }



    @Test
    void findAll_devolverTodasLasTarjetas_ConParamtroNombre() {
        log.info("devolver Todas Las Tarjetas con parametro nombre");
        String nombre = "GTA VI";
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos1);

        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByNombre(nombre)).thenReturn(listaVideojuegos);

        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(nombre,null);

        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        verify(juegosRepository, times(1)).findByNombre(nombre);
    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametroCliente(){
        log.info("devolver Todas Las Tarjetas con parametro cliente");

        String cliente = "juan";
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos2);
        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByClienteContainsIgnoreCase(cliente)).thenReturn(listaVideojuegos);

        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(null,cliente);

        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        verify(juegosRepository, times(1)).findByClienteContainsIgnoreCase(cliente);

    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametrosNombreYCliente(){
        log.info("devolver Todas Las Tarjetas con parametros nombre y cliente");

        String nombre = "The Witcher 4";
        String cliente = "maria";
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos2);
        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByNombreAndClienteContainsIgnoreCase(nombre, cliente)).thenReturn(listaVideojuegos);

        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(nombre,cliente);

        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        verify(juegosRepository, times(1)).findByNombreAndClienteContainsIgnoreCase(nombre, cliente);

    }


    @Test
    void findByIdParametroIdValido() {
        log.info("Buscar videojuego por Id con parametro Id valido");

        Long id = 1L;

        VideoJuegosResponseDto expectedVideoJuegoResponses = videoJuegosResponse1;
        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        VideoJuegosResponseDto actualVideoJuegosResponseDto = juegosService.findById(id);

        assertEquals(expectedVideoJuegoResponses ,actualVideoJuegosResponseDto);

        verify(juegosRepository, only()).findById(id);
    }

    @Test
    void findByIdParametroIdNoValido() {
        log.info("Buscar videojuego por Id con parametro Id no valido");

        Long id = 10L;

        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        var resultado = assertThrows(VideoJuegosNotFound.class, () -> juegosService.findById(id));

        assertEquals("VideoJuegos con id " + id + " no encontrada", resultado.getMessage());

        verify(juegosRepository, only()).findById(id);
    }

    @Test
    void saveVideoJuegosConValidosParametros() {
        log.info("Guardando Videojuego con parametros validos");

        VideoJuegosCreateDto videoJuegosCreateDto = VideoJuegosCreateDto.builder()
                .nombre("FC 26")
                .cliente("Pedro")
                .precio(100.0)
                .fecha_lanzamiento(LocalDate.of(2026, 9, 19))
                .genero("Deportes")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(3)
                .build();
        when(clienteService.findByNombre("Pedro")).thenReturn(cliente3);

        VideoJuegos expectedVideoJuegos = VideoJuegos.builder()
                .id(1L)
                .cliente(cliente3)
                .nombre("FC 26")
                .precio(100.0)
                .fecha_lanzamiento(LocalDate.of(2026, 9, 19))
                .genero("Deportes")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(3)
                .build();

        VideoJuegosResponseDto expectedVideoJuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDto(expectedVideoJuegos);

        when(juegosRepository
                .save(any(VideoJuegos.class)))
                .thenReturn(expectedVideoJuegos);

        VideoJuegosResponseDto actualVideoJuegosResponseDto = juegosService.save(videoJuegosCreateDto);

        assertEquals(expectedVideoJuegosResponseDto, actualVideoJuegosResponseDto);

        verify(juegosRepository)
                .save(videoJuegosCaptor
                        .capture());

        VideoJuegos capturedVideoJuegos = videoJuegosCaptor.getValue();
        assertEquals(expectedVideoJuegos.getNombre(), capturedVideoJuegos.getNombre());
    }

    @Test
    void update_VideoJuego_ConIdValida() {
        log.info("Actualizando Videojuego con parametro valido");

        Long id = 1L;

        String nombre = "GTA VI - Edición Especial";

        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        VideoJuegosUpdateDto videoJuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre(nombre)
                .build();

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegos1);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(videoJuegoActualizado);

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosResponse1;

        expectedVideoJuegosResponse.setNombre(nombre);



        VideoJuegosResponseDto actualVideoJuegosResponse = juegosService.update(id, videoJuegosUpdateDto);

        assertThat(actualVideoJuegosResponse)
        .usingRecursiveComparison()
                .ignoringFields("fecha_lanzamiento", "precio", "genero", "plataforma", "edad")
                .isEqualTo(expectedVideoJuegosResponse);

        verify(juegosRepository).findById(id);
        verify(juegosRepository).save(any());

    }

    @Test
    void update_VideoJuego_ConIdNoValida() {
        log.info("Actualizando Videojuego con parametro no valido");

        Long id = 10L;

        VideoJuegosUpdateDto videoJuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre("GTA VI - Edición Especial")
                .build();

        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> juegosService.update(id, videoJuegosUpdateDto))
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessage("VideoJuegos con id " + id + " no encontrada"
        );

        verify(juegosRepository).findById(id);
        verify(juegosRepository, never()).save(any());

    }

    @Test
    void deleteByIdConParametroValido() {
        log.info("Eliminando Videojuego con parametro valido");

        Long id = 1L;

        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        assertThatCode(() -> juegosService.deleteById(id))
                .doesNotThrowAnyException();

        verify(juegosRepository).deleteById(id);

    }

    @Test
    void deleteByIdConParametroNoValido() {
        log.info("Eliminando Videojuego con parametro no valido");

        Long id = 10L;

        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        var resultado = assertThrows(VideoJuegosNotFound.class, () -> juegosService.deleteById(id));

        assertThatThrownBy(() -> juegosService.deleteById(id))
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessage("VideoJuegos con id " + id + " no encontrada");

        verify(juegosRepository, never()).deleteById(id);
    }
}