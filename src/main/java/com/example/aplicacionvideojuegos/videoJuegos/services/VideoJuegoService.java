package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface VideoJuegoService {

    List<VideoJuegos> findAll(String nombre, String genero, VideoJuegos.Plataforma plataforma);

    VideoJuegos findById(Long id);

    VideoJuegosResponseDto save(VideoJuegosCreateDto  videoJuegosCreateDto);

    VideoJuegos update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto);

    void deleteById(Long id);



}
