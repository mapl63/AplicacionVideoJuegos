package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.rest.clientes.services.ClienteService;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.rest.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.rest.videoJuegos.repositories.VideoJuegosRepository;

import com.example.aplicacionvideojuegos.config.webSockets.WebSocketConfig;
import com.example.aplicacionvideojuegos.config.webSockets.WebSocketHandler;
import com.example.aplicacionvideojuegos.rest.videoJuegos.services.VideoJuegosServiceImpl;
import com.example.aplicacionvideojuegos.webSockets.notifications.mappers.VideoJuegosNotificationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;


import java.io.IOException;
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

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private VideoJuegosNotificationMapper videoJuegosNotificationMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketHandler webSocketService;


    @BeforeEach
    void setUp() {

        videoJuegosResponse1 = videoJuegosMapper.toVideoJuegosResponseDto(videoJuegos1);

        juegosService.setWebSocketService(webSocketService);
    }



    @Test
    void findAll_devolverTodasLasTarjetas_NoPasarNingunParametro() {
        log.info("devolver Todas Las Tarjetas sin pasar ningun parametro");

        List <VideoJuegos> expectedJuego = Arrays.asList(videoJuegos1, videoJuegos2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<VideoJuegos> expectedPage = new PageImpl<>(expectedJuego);
        when(juegosRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<VideoJuegosResponseDto> actualPage =
                juegosService.findAll(Optional.empty(), Optional.empty(),Optional.empty(), pageable);

        assertAll("findAll_devolverTodasLasTarjetas_NoPasarNingunParametro",
                () -> assertNotNull(actualPage),
                () ->  assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(juegosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));


    }



    @Test
    void findAll_devolverTodasLasTarjetas_ConParamtroNombre() {
        log.info("devolver Todas Las Tarjetas con parametro nombre");

        Optional<String> nombre = Optional.of("GTA VI");

        List <VideoJuegos> expectedJuegos = List.of(videoJuegos1);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<VideoJuegos> page = new PageImpl<>(expectedJuegos);
        when(juegosRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(page);

        Page<VideoJuegosResponseDto> actualPage =
                juegosService.findAll(nombre, Optional.empty(),Optional.empty(), pageable);

        assertAll("findAll_devolverTodasLasTarjetas_ConParamtroNombre",
                () -> assertNotNull(actualPage),
                () ->  assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(juegosRepository, only()).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametroCliente(){
        log.info("devolver Todas Las Tarjetas con parametro cliente");

        Optional<String> cliente = Optional.of("maria");

        List <VideoJuegos> expectedJuegos = List.of(videoJuegos2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<VideoJuegos> expectedPage = new PageImpl<>(List.of(videoJuegos2));
        when(juegosRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<VideoJuegosResponseDto> actualPage =
                juegosService.findAll(Optional.empty(), cliente,Optional.empty(), pageable);

        assertAll("findAll_devolverTodasLasTarjetas_ConParametroCliente",
                () -> assertNotNull(actualPage),
                () ->  assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(juegosRepository, only()).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametrosNombreYCliente(){
        log.info("devolver Todas Las Tarjetas con parametros nombre y cliente");

        Optional<String> nombre = Optional.of("The Witcher 4");
        Optional<String> cliente = Optional.of("maria");

         List <VideoJuegos> expectedJuegos = List.of(videoJuegos2);

         Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<VideoJuegos> expectedPage = new PageImpl<>(List.of(videoJuegos2));
        when(juegosRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<VideoJuegosResponseDto> actualPage =
                juegosService.findAll(nombre, cliente,Optional.empty(), pageable);

        assertAll("findAll_devolverTodasLasTarjetas_ConParametrosNombreYCliente",
                () -> assertNotNull(actualPage),
                () ->  assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        verify(juegosRepository, only()).findAll(
                any(Specification.class),
                any(Pageable.class)
        );

    }


    @Test
    void findByIdParametroIdValido() {
        log.info("Buscar videojuego por Id con parametro Id valido");

        Long id = 1L;

        VideoJuegosResponseDto expectedVideoJuegoResponses = videoJuegosResponse1;
        // Arrange: el repositorio devuelve la entidad correspondiente
        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        // Act: consultamos el servicio
        VideoJuegosResponseDto actualVideoJuegosResponseDto = juegosService.findById(id);

        // Assert: el DTO devuelto coincide con el esperado
        assertEquals(expectedVideoJuegoResponses ,actualVideoJuegosResponseDto);

        // Verify: solo se invoca la búsqueda por ID
        verify(juegosRepository, only()).findById(id);
    }

    @Test
    void findByIdParametroIdNoValido() {
        log.info("Buscar videojuego por Id con parametro Id no valido");

        Long id = 10L;

        // Arrange: el repositorio no encuentra la entidad
        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert: el servicio debe lanzar VideoJuegosNotFound
        var resultado = assertThrows(VideoJuegosNotFound.class, () -> juegosService.findById(id));

        assertEquals("VideoJuegos con id " + id + " no encontrada", resultado.getMessage());

        // Verify: solo se consultó al repositorio
        verify(juegosRepository, only()).findById(id);
    }

    @Test
    void saveVideoJuegosConValidosParametros() throws IOException {
        log.info("Guardando Videojuego con parametros validos");

        // Arrange: DTO de creación y comportamientos del repositorio/servicios
        VideoJuegosCreateDto videoJuegosCreateDto = VideoJuegosCreateDto.builder()

                .cliente("Pedro")
                .nombre("FC 26")
                .precio(100.0)
                .fecha_lanzamiento(LocalDate.of(2026, 9, 19))
                .genero("DEPORTES")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(3)
                .build();


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

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosMapper.toVideoJuegosResponseDto(expectedVideoJuegos);
        when(clienteService.findByNombre(videoJuegosCreateDto.getCliente())).thenReturn(cliente3);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(expectedVideoJuegos);
        doNothing().when(webSocketService).sendMessage(any());

        // Act: invocamos el servicio para guardar el videojuego
        VideoJuegosResponseDto actualVideoJuegosResponse = juegosService.save(videoJuegosCreateDto);

        // Assert: el DTO devuelto es igual al esperado
        assertEquals(expectedVideoJuegosResponse, actualVideoJuegosResponse);

        // Verify: capturamos la entidad persistida para revisar sus campos
        verify(juegosRepository).save(videoJuegosCaptor.capture());

        VideoJuegos capturedVideoJuegos = videoJuegosCaptor.getValue();

        assertEquals(expectedVideoJuegos.getNombre(), capturedVideoJuegos.getNombre());
    }

    @Test
    void update_VideoJuego_ConIdValida() throws IOException {
        log.info("Actualizando Videojuego con parametro valido");

        Long id = 2L;

        String nombre = "GTA VI - Edición Especial";

        // Arrange: existe el videojuego y se mapeará con los nuevos datos
        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        VideoJuegosUpdateDto videoJuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre(nombre)
                .build();

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegos1);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(videoJuegoActualizado);

        videoJuegosResponse1.setNombre(nombre);

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosResponse1;
        doNothing().when(webSocketService).sendMessage(any());

        // Act: actualizamos el videojuego
        VideoJuegosResponseDto actualVideoJuegosResponse = juegosService.update(id, videoJuegosUpdateDto);

        // Assert: el resultado coincide con lo esperado (ignorando campos no modificados)
        assertThat(actualVideoJuegosResponse)
        .usingRecursiveComparison()
                .ignoringFields("fecha_lanzamiento", "precio", "genero", "plataforma", "edad")
                .isEqualTo(expectedVideoJuegosResponse);

        // Verify: se consulta y guarda exactamente una vez
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

        // Arrange: el repositorio no encuentra el videojuego
        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert: se lanza VideoJuegosNotFound
        assertThatThrownBy(
                () -> juegosService.update(id, videoJuegosUpdateDto))
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessage("VideoJuegos con id " + id + " no encontrada"
        );

        // Verify: no se intenta guardar nada
        verify(juegosRepository).findById(id);
        verify(juegosRepository, never()).save(any());

    }

    @Test
    void deleteByIdConParametroValido() throws IOException {

        log.info("Eliminando Videojuego con parametro valido");

        long id = 7L;

        VideoJuegos videoJuegosAEliminar = VideoJuegos.builder()
                .id(id)
                .cliente(cliente1)
                .nombre("GTA VI")
                .precio(120.0)
                .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(18)
                .build();

        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegosAEliminar));
        doNothing().when(webSocketService).sendMessage(any());

        assertThatCode(() -> juegosService.deleteById(id))
                .doesNotThrowAnyException();

        verify(juegosRepository).deleteById(id);

    }

    @Test
    void deleteByIdConParametroNoValido() {
        log.info("Eliminando Videojuego con parametro no valido");

        Long id = 10L;

        // Arrange: el repositorio no encuentra la entidad
        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert: se lanza la excepción esperada

        assertThatThrownBy(() -> juegosService.deleteById(id))
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessage("VideoJuegos con id " + id + " no encontrada");

        // Verify: no se ejecuta el borrado
        verify(juegosRepository, never()).deleteById(id);
    }
}