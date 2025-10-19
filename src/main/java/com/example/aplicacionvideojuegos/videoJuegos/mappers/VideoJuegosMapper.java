package com.example.aplicacionvideojuegos.videoJuegos.mappers;

import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import org.springframework.stereotype.Component;

@Component
public class VideoJuegosMapper {
    public VideoJuegos toVideoJuegos(Long id, VideoJuegosCreateDto videojuegosCreateDto) {
        return new  VideoJuegos(
          id,
          videojuegosCreateDto.getNombre(),
          videojuegosCreateDto.getPrecio(),
          videojuegosCreateDto.getFecha_lanzamiento(),
          videojuegosCreateDto.getGenero(),
          videojuegosCreateDto.getPlataforma(),
          videojuegosCreateDto.getEdad()
        );
    }

    public VideoJuegos toVideoJuegos(VideoJuegosUpdateDto videojuegosUpdateDto, VideoJuegos videojuegos) {
        return new  VideoJuegos(
                videojuegos.getId(),
                videojuegosUpdateDto.getNombre() != null ? videojuegosUpdateDto.getNombre() : videojuegos.getNombre(),
                videojuegosUpdateDto.getPrecio() != null ? videojuegosUpdateDto.getPrecio() : videojuegos.getPrecio(),
                videojuegosUpdateDto.getFecha_lanzamiento() != null ? videojuegosUpdateDto.getFecha_lanzamiento() : videojuegos.getFecha_lanzamiento(),
                videojuegosUpdateDto.getGenero() != null ? videojuegosUpdateDto.getGenero() : videojuegos.getGenero(),
                videojuegosUpdateDto.getPlataforma() != null ? videojuegosUpdateDto.getPlataforma() : videojuegos.getPlataforma(),
                videojuegosUpdateDto.getEdad() != null ? videojuegosUpdateDto.getEdad() : videojuegos.getEdad()
        );
    }

    public VideoJuegosResponseDto toVideoJuegosResponseDto(VideoJuegos videojuegos) {
        return new  VideoJuegosResponseDto(
                videojuegos.getId(),
                videojuegos.getNombre(),
                videojuegos.getPrecio(),
                videojuegos.getFecha_lanzamiento(),
                videojuegos.getGenero(),
                videojuegos.getPlataforma(),
                videojuegos.getEdad()
        );
    }

}
