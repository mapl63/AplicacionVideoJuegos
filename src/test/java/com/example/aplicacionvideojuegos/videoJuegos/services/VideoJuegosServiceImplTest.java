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


/**
 * Pruebas unitarias de {@link VideoJuegosServiceImpl} usando Mockito.
 * Se mockea el repositorio, el servicio de clientes y se espía el mapper real.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class VideoJuegosServiceImplTest {

    /**
     * Clientes base utilizados en los escenarios de prueba.
     */
    private final Cliente cliente1 = Cliente.builder()
            .nombre("Juan")
            .build();
    private final Cliente cliente2 = Cliente.builder()
            .nombre("Maria")
            .build();

    private final Cliente cliente3 = Cliente.builder()
            .nombre("Pedro")
            .build();

    /**
     * Entidades de videojuegos que sirven como fixtures.
     */
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
        // Arrange: el repositorio devuelve ambas entidades
        List <VideoJuegos> listaVideojuegos = Arrays.asList(videoJuegos1, videoJuegos2);

        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findAll()).thenReturn(listaVideojuegos);

        // Act: invocamos el servicio sin filtros
        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(null,null);

        // Assert: la respuesta coincide con la conversión esperada
        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        // Verify: solo se usa findAll del repositorio
        verify(juegosRepository, times(1)).findAll();

    }



    @Test
    void findAll_devolverTodasLasTarjetas_ConParamtroNombre() {
        log.info("devolver Todas Las Tarjetas con parametro nombre");
        String nombre = "GTA VI";
        // Arrange: solo se devuelve el videojuego con ese nombre
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos1);

        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByNombre(nombre)).thenReturn(listaVideojuegos);

        // Act: se consulta con el filtro de nombre
        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(nombre,null);

        // Assert: la respuesta coincide con la esperada
        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        // Verify: se usa el método correcto del repositorio
        verify(juegosRepository, times(1)).findByNombre(nombre);
    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametroCliente(){
        log.info("devolver Todas Las Tarjetas con parametro cliente");

        String cliente = "juan";
        // Arrange: el repositorio devuelve los videojuegos del cliente
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos2);
        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByClienteContainsIgnoreCase(cliente)).thenReturn(listaVideojuegos);

        // Act: se invoca el servicio filtrando por cliente
        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(null,cliente);

        // Assert: la lista resultante coincide con la esperada
        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        // Verify: solo se usa la consulta por cliente
        verify(juegosRepository, times(1)).findByClienteContainsIgnoreCase(cliente);

    }

    @Test
    void findAll_devolverTodasLasTarjetas_ConParametrosNombreYCliente(){
        log.info("devolver Todas Las Tarjetas con parametros nombre y cliente");

        String nombre = "The Witcher 4";
        String cliente = "maria";
        // Arrange: se espera obtener solo el videojuego que cumple ambos filtros
        List <VideoJuegos> listaVideojuegos = List.of(videoJuegos2);
        List<VideoJuegosResponseDto> listaVideojuegosResponseDto = videoJuegosMapper.toVideoJuegosResponseDtoList(listaVideojuegos);
        when (juegosRepository.findByNombreAndClienteContainsIgnoreCase(nombre, cliente)).thenReturn(listaVideojuegos);

        // Act: invocamos el servicio con ambos parámetros
        List <VideoJuegosResponseDto> actualVideoJuegoResponses = juegosService.findAll(nombre,cliente);

        // Assert: la lista devuelta coincide con la conversión esperada
        assertIterableEquals(listaVideojuegosResponseDto, actualVideoJuegoResponses);

        // Verify: se usa la consulta por nombre y cliente
        verify(juegosRepository, times(1)).findByNombreAndClienteContainsIgnoreCase(nombre, cliente);

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
    void saveVideoJuegosConValidosParametros() {
        log.info("Guardando Videojuego con parametros validos");

        // Arrange: DTO de creación y comportamientos del repositorio/servicios
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

        // Act: invocamos el servicio para guardar el videojuego
        VideoJuegosResponseDto actualVideoJuegosResponseDto = juegosService.save(videoJuegosCreateDto);

        // Assert: el DTO devuelto es igual al esperado
        assertEquals(expectedVideoJuegosResponseDto, actualVideoJuegosResponseDto);

        // Verify: capturamos la entidad persistida para revisar sus campos
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

        // Arrange: existe el videojuego y se mapeará con los nuevos datos
        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        VideoJuegosUpdateDto videoJuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre(nombre)
                .build();

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegos1);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(videoJuegoActualizado);

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosResponse1;

        expectedVideoJuegosResponse.setNombre(nombre);



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
    void deleteByIdConParametroValido() {
        log.info("Eliminando Videojuego con parametro valido");

        Long id = 1L;

        // Arrange: el repositorio encuentra la entidad antes de borrarla
        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        // Act & Assert: eliminar no debe lanzar excepciones
        assertThatCode(() -> juegosService.deleteById(id))
                .doesNotThrowAnyException();

        // Verify: se invoca a deleteById
        verify(juegosRepository).deleteById(id);

    }

    @Test
    void deleteByIdConParametroNoValido() {
        log.info("Eliminando Videojuego con parametro no valido");

        Long id = 10L;

        // Arrange: el repositorio no encuentra la entidad
        when(juegosRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert: se lanza la excepción esperada
        var resultado = assertThrows(VideoJuegosNotFound.class, () -> juegosService.deleteById(id));

        assertThatThrownBy(() -> juegosService.deleteById(id))
                .isInstanceOf(VideoJuegosNotFound.class)
                .hasMessage("VideoJuegos con id " + id + " no encontrada");

        // Verify: no se ejecuta el borrado
        verify(juegosRepository, never()).deleteById(id);
    }
}