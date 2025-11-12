package com.example.aplicacionvideojuegos.videoJuegos.services;

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

    @Override
    public List<VideoJuegosResponseDto> findAll(String nombre, String genero, VideoJuegos.Plataforma plataforma) {
        //Mostrar todos los juegos
        if((nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty()) && (plataforma == null)) {
            log.info("Buscamos todos los videojuegos");
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findAll());
        }

        //Mostrar juegos por nombre
        if ((nombre != null && !nombre.isEmpty()) && (genero == null || genero.isEmpty()) &&  (plataforma == null)) {
            log.info("Buscamos los videojuegos por nombre: " + nombre);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findAllByNombre(nombre));
        }

        //Mostrar juegos por genero
        if((genero != null && !genero.isEmpty()) && (nombre == null || nombre.isEmpty()) && (plataforma == null)) {
            log.info("Buscamos todos los videojuegos por genero: " + genero);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findAllByGenero(genero));
        }

        //Mostrar por plataforma
        if((plataforma != null)&&(nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty())) {
            log.info("Buscamos los videojuegos por plataforma: " + plataforma);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findAllByPlataforma(plataforma));
        }

        //Mostrar por nombre y plataforma
        if((nombre != null && !nombre.isEmpty()) && (plataforma != null) && (genero == null || genero.isEmpty())) {
            log.info("Buscamos los videojuegos por nombre: " + nombre +  " y por plataforma: " + plataforma);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByNombreAndPlataforma(nombre, plataforma));
        }

        //Mostrar por genero y plataforma
        if ((genero != null && !genero.isEmpty()) && (plataforma != null) && (nombre == null || nombre.isEmpty())) {
            log.info("Buscamos por genero" + genero + " y plataforma: " + plataforma);
            return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByGeneroAndPlataforma(genero, plataforma));
        }

        log.info("Buscamos el videojuego por nombre: " + nombre + " por genero: " + genero);
        return videoJuegosMapper.toVideoJuegosResponseDtoList(videoJuegosRepository.findByNombreAndGenero(nombre, genero));

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

        Long id = videoJuegosRepository.nextId();

        VideoJuegos videojuegoNuevo = videoJuegosMapper.toVideoJuegosCreated(id, videoJuegosCreateDto);

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
        log.info("Eliminando el VideoJuego por id: {}" ,id);

        videoJuegosRepository.findById(id).orElseThrow(() -> new VideoJuegosNotFound(id));

        videoJuegosRepository.deleteById(id);
    }
}
