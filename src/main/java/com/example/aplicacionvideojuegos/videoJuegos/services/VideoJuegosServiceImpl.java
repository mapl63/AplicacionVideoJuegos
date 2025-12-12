package com.example.aplicacionvideojuegos.videoJuegos.services;

import com.example.aplicacionvideojuegos.clientes.models.Cliente;
import com.example.aplicacionvideojuegos.clientes.services.ClienteService;
import com.example.aplicacionvideojuegos.config.webSockets.WebSocketConfig;
import com.example.aplicacionvideojuegos.config.webSockets.WebSocketHandler;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosCreateDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosResponseDto;
import com.example.aplicacionvideojuegos.videoJuegos.dto.VideoJuegosUpdateDto;
import com.example.aplicacionvideojuegos.videoJuegos.exceptions.VideoJuegosNotFound;
import com.example.aplicacionvideojuegos.videoJuegos.mappers.VideoJuegosMapper;
import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import com.example.aplicacionvideojuegos.videoJuegos.repositories.VideoJuegosRepository;
import com.example.aplicacionvideojuegos.webSockets.notifications.dto.VideoJuegosNotificationResponse;
import com.example.aplicacionvideojuegos.webSockets.notifications.mappers.VideoJuegosNotificationMapper;
import com.example.aplicacionvideojuegos.webSockets.notifications.models.Notificacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@CacheConfig(cacheNames = {"videoJuegos"})
@Slf4j
@RequiredArgsConstructor
@Service
public class VideoJuegosServiceImpl implements VideoJuegoService, InitializingBean {

    private final VideoJuegosRepository videoJuegosRepository;
    private final VideoJuegosMapper videoJuegosMapper;
    private final ClienteService clienteService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final VideoJuegosNotificationMapper videoJuegosNotificationMapper;
    private WebSocketHandler webSocketService;

    public void afterPropertiesSet() {

        this.webSocketService = this.webSocketConfig.webSocketVideoJuegosHandler();
    }

    public void setWebSocketService(WebSocketHandler webSocketHandler) {

        this.webSocketService = webSocketHandler;
    }

    @Override
    public Page<VideoJuegosResponseDto> findAll(Optional<String> nombre, Optional<String> cliente, Optional<Boolean> isDeleted, Pageable pageable){
        log.info("Buscando todos los VideoJuegos con nombre: {}, cliente: {}, isDeleted: {}" , nombre, cliente, isDeleted);

        Specification<VideoJuegos> specNombreVideoJuego = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<VideoJuegos> specClienteVideoJuego = (root, query, criteriaBuilder) ->
                cliente.map(c -> {
                    Join<VideoJuegos, Cliente> clienteJoin = root.join("cliente");
                    return criteriaBuilder.like(criteriaBuilder.lower(clienteJoin.get("nombre")), "%" + c.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<VideoJuegos> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<VideoJuegos> criterio = Specification.allOf(specNombreVideoJuego, specClienteVideoJuego, specIsDeleted);

        return videoJuegosRepository.findAll(criterio, pageable)
                .map(videoJuegosMapper::toVideoJuegosResponseDto);


    }

    @Cacheable(key = "#id")
    @Override
    public VideoJuegosResponseDto findById(Long id){
        log.info("Buscando tarjeta por id {}", id);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.findById(id)
                .orElseThrow(() -> new VideoJuegosNotFound(id)));

    }

    @Override
    public Page<VideoJuegosResponseDto> findByUsuarioId(Long id, Pageable pageable) {
        log.info("Buscando todos los VideoJuegos del usuario con id: {}", id);
        return videoJuegosRepository.findByUsuarioId(id, pageable)
                .map(videoJuegosMapper::toVideoJuegosResponseDto);
    }

    @CachePut(key = "#result.id")
    @Override
    public VideoJuegosResponseDto save(VideoJuegosCreateDto videoJuegosCreateDto) {
        log.info("Guardando nuevo VideoJuego: {}" ,  videoJuegosCreateDto);

        var cliente = clienteService.findByNombre(videoJuegosCreateDto.getCliente());

        VideoJuegos videojuegoNuevo = videoJuegosRepository.save(
                videoJuegosMapper.toVideoJuegosCreated(videoJuegosCreateDto, cliente));

        onChange(Notificacion.Tipo.CREATE, videojuegoNuevo);

        return videoJuegosMapper.toVideoJuegosResponseDto(videojuegoNuevo);
    }

    @CachePut(key = "#result.id")
    @Override
    public VideoJuegosResponseDto update(Long id, VideoJuegosUpdateDto videoJuegosUpdateDto) {
        log.info("Actualizamos el VideoJuegos por id: {} ", id);

        var videoJuegoActual = videoJuegosRepository.findById(id).orElseThrow(() -> new VideoJuegosNotFound(id));

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegoActual);

        onChange(Notificacion.Tipo.UPDATE, videoJuegoActualizado);

        return videoJuegosMapper.toVideoJuegosResponseDto(videoJuegosRepository.save(videoJuegoActualizado));
    }

    @CacheEvict(key = "#id")
    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando el VideoJuego por id: {}" ,id);

        VideoJuegos videoJuegos = videoJuegosRepository.findById(id).orElseThrow(() -> new VideoJuegosNotFound(id));

        videoJuegosRepository.deleteById(id);

        onChange(Notificacion.Tipo.DELETE, videoJuegos);
    }

    void onChange(Notificacion.Tipo tipo, VideoJuegos data) {
        log.debug("Servicio de productos onChange con tipo: {} y datos:  id={}, nombre = {}", tipo, data.getId(), data.getNombre());

        if (webSocketService == null){
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketVideoJuegosHandler();
        }

        try {
            Notificacion<VideoJuegosNotificationResponse> notificacion = new Notificacion<>(
                    "VideoJuegos",
                    tipo,
                    videoJuegosNotificationMapper.toVideoJuegosNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString(notificacion);

            log.info("Enviando mensaje a los clientes ws");

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a los clientes ws", e);
                }
            });

            senderThread.setName("WebSocketVideoJuegos-" + data.getId());
            senderThread.setDaemon(true);
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}" , data.getId());
        }catch (JsonProcessingException e){
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}