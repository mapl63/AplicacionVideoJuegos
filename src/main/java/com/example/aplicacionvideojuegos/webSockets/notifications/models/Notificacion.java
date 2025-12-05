package com.example.aplicacionvideojuegos.webSockets.notifications.models;

public record Notificacion<VJ>(
        String entity,   // Qué tipo de cosa se está modificando, en tu caso "VideoJuegos" (VJ).
        Tipo type,       // Qué acción pasó: CREATE = se creó, UPDATE = se modificó, DELETE = se borró.
        VJ data,         // Los **datos reales** de la entidad, normalmente en formato DTO, lo que le vamos a mostrar al usuario.
        String createdAt // La fecha y hora en que ocurrió la acción, para que el usuario sepa cuándo pasó.

){
    public enum Tipo {
        CREATE,
        UPDATE,
        DELETE
    }
}
