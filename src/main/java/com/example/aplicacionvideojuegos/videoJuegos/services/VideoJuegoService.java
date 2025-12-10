package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;


public interface VideoJuegoService {

    Page<VideoJuegosResponseDto> findAll(Optional<String> nombre, Optional<String> cliente, Optional<Boolean> isDeleted , Pageable pageable);

    VideoJuegosResponseDto findById(Long id);

    VideoJuegosResponseDto save(VideoJuegosCreateDto  videoJuegosCreateDto);

    VideoJuegosResponseDto update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto);

    void deleteById(Long id);



}
