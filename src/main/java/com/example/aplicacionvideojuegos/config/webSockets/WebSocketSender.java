package com.example.aplicacionvideojuegos.config.webSockets;

import java.io.IOException;

public interface WebSocketSender {
    void sendMessage(String message) throws IOException;

    void sendPeriodicMessages() throws IOException;

}
