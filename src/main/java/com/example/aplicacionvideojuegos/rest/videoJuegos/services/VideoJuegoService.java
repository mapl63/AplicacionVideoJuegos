package com.example.aplicacionvideojuegos.rest.videoJuegos.services;

import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;


public interface VideoJuegoService {

    Page<VideoJuegosResponseDto> findAll(Optional<String> nombre,
                                         Optional<String> cliente,
                                         Optional<Boolean> isDeleted,
                                         Pageable pageable);

    VideoJuegosResponseDto findById(Long id);

    Page<VideoJuegosResponseDto> findByUsuarioId(Long id, Pageable pageable);

    VideoJuegosResponseDto save(VideoJuegosCreateDto  videoJuegosCreateDto);

    VideoJuegosResponseDto update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto);

    List<VideoJuegos> buscarPorUsuarioId(Long clienteId);

    Optional<VideoJuegos> buscarPorId(Long id);

    void deleteById(Long id);

}
