package com.example.aplicacionvideojuegos.config.webSockets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@Slf4j
public class WebSocketHandler extends TextWebSocketHandler  implements SubProtocolCapable, WebSocketSender {

    private final String entity;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión WebSocket establecida para la entidad: {}", entity);
        log.info("Sesión: {}", session);

        sessions.add(session);

        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - (App de VideoJuegos)");

        log.info("Servidor enviá: {}", message);

        session.sendMessage(message);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Conexión cerrada con el servidor: {}",status);
        sessions.remove(session);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        log.info("Enviar mensaje de cambios en la entidad: {} : {}",entity, message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                log.info("Servidor WS enviá: {}", message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void sendPeriodicMessages() throws IOException {
        log.info("Enviando mensajes para la entidad: {}", entity);
        for (WebSocketSession session : sessions){
            if (session.isOpen()){
                String broadcast = "server periodic message " + LocalTime.now();
                log.info("Servidor sends: {}", broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();

        log.info("Server received: " + request);
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));

        log.info("Server sends: " + response);
        session.sendMessage(new TextMessage(response));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: {}" , exception.getMessage());
    }

    @Override
    public List<String> getSubProtocols() {
        return List.of("supprotocol.demo.websocket");
    }

}