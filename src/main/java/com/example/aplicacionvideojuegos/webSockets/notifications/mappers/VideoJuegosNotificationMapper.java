package com.example.aplicacionvideojuegos.webSockets.notifications.mappers;

import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.webSockets.notifications.dto.VideoJuegosNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class VideoJuegosNotificationMapper {

    public VideoJuegosNotificationResponse toVideoJuegosNotificationDto(VideoJuegos videoJuegos) {
        return new VideoJuegosNotificationResponse(
                videoJuegos.getId(),
                videoJuegos.getCliente().getNombre(),
                videoJuegos.getNombre(),
                videoJuegos.getPrecio(),
                videoJuegos.getFecha_lanzamiento().toString(),
                videoJuegos.getGenero(),
                videoJuegos.getPlataforma().toString()
        );
    }
}
