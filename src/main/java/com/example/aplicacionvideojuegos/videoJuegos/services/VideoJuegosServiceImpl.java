package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.clientes.services.ClienteService;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.repositories.VideoJuegosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = {"videoJuegos"})
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoJuegosServiceImpl implements VideoJuegoService{

    private final VideoJuegosRepository videoJuegosRepository;
    private final VideoJuegosMapper videoJuegosMapper;
    private final ClienteService clienteService;

    @Override
    public List<VideoJuegosResponseDto> findAll(String nombre, String cliente) {
        //Mostrar todos los juegos
        if((nombre == null || nombre.isEmpty()) && (cliente == null || cliente.isEmpty())){
            log.info("Buscando todos los VideoJuegos");
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findAll());
        }

        //Mostrar por nombre
        if((nombre != null && !nombre.isEmpty()) && (cliente == null || cliente.isEmpty())){
            log.info("Buscando VideoJuegos por nombre: {}", nombre);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByNombre(nombre));
        }

        //Mostrar por cliente
        if(nombre == null || nombre.isEmpty()){
            log.info("Buscando VideoJuegos por cliente: {}", cliente);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByClienteContainsIgnoreCase(cliente));
        }

        //Mostrar por nombre y cliente
        log.info("Buscando VideoJuegos por nombre: {} y cliente: {}", nombre, cliente);
        return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByNombreAndClienteContainsIgnoreCase(nombre, cliente));

    }

    @Cacheable(key = "#id")
    @Override
    public VideoJuegosResponseDto findById(Long id){
        log.info("Buscando tarjeta por id {}", id);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.findById(id)
                .orElseThrow(() -> new VideoJuegosNotFound(id)));

    }

    @CachePut(key = "#result.id")
    @Override
    public VideoJuegosResponseDto save(VideoJuegosCreateDto videoJuegosCreateDto) {
        log.info("Guardando nuevo VideoJuego: {}" ,  videoJuegosCreateDto);

        var cliente = clienteService.findByNombre(videoJuegosCreateDto.getCliente());
        VideoJuegos videojuegoNuevo = videoJuegosMapper.toVideoJuegosCreated(videoJuegosCreateDto, cliente);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.save(videojuegoNuevo));
    }

    @CachePut(key = "#result.id")
    @Override
    public VideoJuegosResponseDto update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto) {
        log.info("Actualizamos el VideoJuegos por id: {} ", id);

        var videoJuegoActual = videoJuegosRepository.findById(id).orElseThrow(() -> new VideoJuegosNotFound(id));

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegoActual);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.save(videoJuegoActualizado));
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando el VideoJuego por id: {}" ,id);

        videoJuegosRepository.findById(id).orElseThrow(() -> new VideoJuegosNotFound(id));

        videoJuegosRepository.deleteById(id);
    }
}
