package com.example.aplicacionvideojuegos.videoJuegos.mappers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class VideoJuegosMapper {
    public VideoJuegos toVideoJuegosCreated(Long id, VideoJuegosCreateDto videojuegosCreateDto) {
        return VideoJuegos.builder()
                .id(id)
                .nombre(videojuegosCreateDto.getNombre())
                .precio(videojuegosCreateDto.getPrecio())
                .fecha_lanzamiento(videojuegosCreateDto.getFecha_lanzamiento())
                .genero(videojuegosCreateDto.getGenero())
                .plataforma(videojuegosCreateDto.getPlataforma())
                .edad(videojuegosCreateDto.getEdad())
                .build();
    }

    public VideoJuegos toVideoJuegosUpdate(VideoJuegosUpdateDto videojuegosUpdateDto, VideoJuegos videojuegos) {
        return VideoJuegos.builder()
                .id(videojuegos.getId())
                .nombre(videojuegosUpdateDto.getNombre() != null ? videojuegosUpdateDto.getNombre() : videojuegos.getNombre())
                .precio(videojuegosUpdateDto.getPrecio() != null ? videojuegosUpdateDto.getPrecio() : videojuegos.getPrecio())
                .genero(videojuegosUpdateDto.getGenero() != null ? videojuegosUpdateDto.getGenero() : videojuegos.getGenero())
                .plataforma(videojuegosUpdateDto.getPlataforma() != null ? videojuegosUpdateDto.getPlataforma() : videojuegos.getPlataforma())
                .edad(videojuegosUpdateDto.getEdad() != null ? videojuegosUpdateDto.getEdad() : videojuegos.getEdad())
                .build();

    }

    public VideoJuegosResponseDto toVideoJuegosResponseDto(VideoJuegos videojuegos) {
        log.info("Entrando a VideoJuegosResponseDto para comprobar que entra en el mapper");
        return VideoJuegosResponseDto.builder()
                .id(videojuegos.getId())
                .nombre(videojuegos.getNombre())
                .precio(videojuegos.getPrecio())
                .fecha_lanzamiento(videojuegos.getFecha_lanzamiento())
                .genero(videojuegos.getGenero())
                .plataforma(videojuegos.getPlataforma())
                .edad(videojuegos.getEdad())
                .build();

    }

    public List<VideoJuegosResponseDto> toVideoJuegosResponseDtoList(List<VideoJuegos> videojuegos) {
        return videojuegos.stream()
                .map(this::toVideoJuegosResponseDto)
                .toList();
    }
}
