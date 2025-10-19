package com.example.aplicacionvideojuegos.videoJuegos.repositories;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;

import java.util.List;
import java.util.Optional;

public interface VideoJuegosRepository {

    List<VideoJuegos> findAll();

    List<VideoJuegos> findAllByNombre(String nombre);

    List<VideoJuegos> findAllByGenero(String genero);

    List<VideoJuegos> findAllByPlataforma(VideoJuegos.Plataforma plataforma);

    List<VideoJuegos> findByNombreAndGenero(String nombre, String genero);

    List<VideoJuegos> findByNombreAndPlataforma(String nombre, VideoJuegos.Plataforma plataforma);

    List<VideoJuegos> findByGeneroAndPlataforma(String genero, VideoJuegos.Plataforma plataforma);

    Optional<VideoJuegos> findById(Long id);

    void deleteById(Long id);

    VideoJuegos save(VideoJuegos videoJuegos);

    Long nextId();

}
