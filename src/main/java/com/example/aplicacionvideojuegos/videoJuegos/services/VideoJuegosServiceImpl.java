package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.repositories.VideoJuegosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = {"videoJuegos"})
@Slf4j
@Service
public class VideoJuegosServiceImpl implements VideoJuegoService{

    private final VideoJuegosRepository videoJuegosRepository;
    private final VideoJuegosMapper videoJuegosMapper;

    @Autowired
    public VideoJuegosServiceImpl(VideoJuegosRepository videoJuegosRepository,  VideoJuegosMapper videoJuegosMapper) {
        this.videoJuegosRepository = videoJuegosRepository;
        this.videoJuegosMapper = videoJuegosMapper;
    }

    @Override
    public List<VideoJuegos> findAll(String nombre, String genero, VideoJuegos.Plataforma plataforma) {
        //Mostrar todos los juegos
        if((nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty()) && (plataforma == null)) {
            log.info("Buscamos todos los videojuegos");
            return videoJuegosRepository.findAll();
        }

        //Mostrar juegos por nombre
        if ((nombre != null && !nombre.isEmpty()) && (genero == null || genero.isEmpty()) &&  (plataforma == null)) {
            log.info("Buscamos los videojuegos por nombre: " + nombre);
            return videoJuegosRepository.findAllByNombre(nombre);
        }

        //Mostrar juegos por genero
        if((genero != null && !genero.isEmpty()) && (nombre == null || nombre.isEmpty()) && (plataforma == null)) {
            log.info("Buscamos todos los videojuegos por genero: " + genero);
            return videoJuegosRepository.findAllByGenero(genero);
        }

        //Mostrar por plataforma
        if((plataforma != null)&&(nombre == null || nombre.isEmpty()) && (genero == null || genero.isEmpty())) {
            log.info("Buscamos los videojuegos por plataforma: " + plataforma);
            return videoJuegosRepository.findAllByPlataforma(plataforma);
        }

        //Mostrar por nombre y plataforma
        if((nombre != null && !nombre.isEmpty()) && (plataforma != null) && (genero == null || genero.isEmpty())) {
            log.info("Buscamos los videojuegos por nombre: " + nombre +  " y por plataforma: " + plataforma);
            return videoJuegosRepository.findByNombreAndPlataforma(nombre, plataforma);
        }

        //Mostrar por genero y plataforma
        if ((genero != null && !genero.isEmpty()) && (plataforma != null) && (nombre == null || nombre.isEmpty())) {
            log.info("Buscamos por genero" + genero + " y plataforma: " + plataforma);
            return videoJuegosRepository.findByGeneroAndPlataforma(genero, plataforma);
        }

        log.info("Buscamos el videojuego por nombre: " + nombre + " por genero: " + genero);
        return videoJuegosRepository.findByNombreAndGenero(nombre, genero);

    }

    /*@Override
    public List<VideoJuegos> findAllByPlataforma(VideoJuegos.Plataforma plataforma){
        if(plataforma == null){
            log.info("No se encontro la plataforma que buscas, devolviendo todos los videoJuegos");
            return videoJuegosRepository.findAll();
        }

        log.info("Buscando videoJuegos por plataforma: " + plataforma);
        return videoJuegosRepository.findAllByPlataforma(plataforma);
    }*/

    @Override
    @Cacheable
    public VideoJuegos findById(Long id){

        Optional<VideoJuegos> videoJuegosEncontrado = videoJuegosRepository.findById(id);
        if(videoJuegosEncontrado.isPresent()){
            return videoJuegosEncontrado.get();
        }else {
            throw new VideoJuegosNotFound(id);
        }
    }

    @Override
    @CachePut
    public VideoJuegosResponseDto save(VideoJuegosCreateDto videoJuegosCreateDto) {
        log.info("Guardando nuevo VideoJuego: " +  videoJuegosCreateDto);

        Long id = videoJuegosRepository.nextId();

        VideoJuegos videojuegoNuevo = videoJuegosMapper.toVideoJuegos(id, videoJuegosCreateDto);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.save(videojuegoNuevo));
    }

    @Override
    @CachePut
    public VideoJuegos update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto) {
        log.info("Actualizamos el VideoJuegos por id: " + id);

        var videoJuegoActual = this.findById(id);

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegos(videoJuegosUpdateDto, videoJuegoActual);

        return videoJuegosRepository.save(videoJuegoActualizado);
    }

    @Override
    @CacheEvict
    public void deleteById(Long id) {
        log.info("Eliminando el VideoJuego por id: " + id);
        var videoJuegoEncontrado = this.findById(id);

        if(videoJuegoEncontrado != null){
            videoJuegosRepository.deleteById(id);
        }
    }
}
