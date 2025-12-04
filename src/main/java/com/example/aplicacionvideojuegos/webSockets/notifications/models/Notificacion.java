package com.example.aplicacionvideojuegos.webSockets.notifications.models;

public record Notificacion<VJ>(
        String entity,
        Tipo type,
        VJ data,
        String createdAt
){
    public enum Tipo {
        CREATE,
        UPDATE,
        DELETE
    }
}
