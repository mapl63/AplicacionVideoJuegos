# WebSockets 
"Son una forma de que el navegador y el servidor hablen entre ellos en tiempo real, sin tener que recargar la página."
Permiten enviar y recibir mensajes instantáneamente en ambas direcciones.
## Lo primero es añadir en el pom.xml su dependencia:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```
### Lo siguiente que hacemos es crear la carpeta webSockets.notifications

### Dentro creamos 3 carpetas:

### models
Donde creamos la clase Notificacion que está enlazada en la entidad VideoJuegos; por eso pongo VJ, indicando la estructura del mensaje que vamos a usar.
```java
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

```
### dto
Seguido creamos la carpeta dto donde tenemos la clase VideoJuegosNotification donde le mostramos los atributos que devolveremos como respuesta al usuario.
```java
public record VideoJuegosNotificationResponse(
        
        Long id,
        String cliente,
        String nombre,
        Double precio,
        String fecha_lanzamiento,
        String genero,
        String plataforma
){ }
```
### mappers
En la carpeta mappers contendra las clases que convierten las entidades de la base de datos en objetos listos para enviar a los usuarios.

En este caso, VideoJuegosNotificationMapper toma un objeto VideoJuegos y lo convierte en un VideoJuegosNotificationResponse, que es el formato que se envía por WebSocket.
```java
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
```
## Después lo que hacemos es crear la carpeta config.webSockets
Creamos la carpeta config.webSockets, donde tendremos 2 clases y 1 interfaz que se encargan de gestionar los mensajes en tiempo real.
### Interfaz WebSocketSender:
```java
public interface WebSocketSender {
    void sendMessage(String message) throws IOException;

    void sendPeriodicMessages() throws IOException;
}
```
### Clase WebSocketHandler 
Se encarga de gestionar las sesiones y enviar los mensajes a los usuarios.
Aquí se definen los métodos que se usarán para enviar datos por WebSocket es decir el gestor del chat:
```java
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler  implements SubProtocolCapable, WebSocketSender {

    private final String entity;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }
```

### 1. Método afterConnectionEstablished:
Este método se ejecuta cuando un cliente se conecta correctamente al WebSocket. Su función es registrar la sesión y enviar un mensaje de bienvenida.
```java
@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión WebSocket establecida para la entidad: {}", entity);
        log.info("Sesión: {}", session);

        sessions.add(session);

        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - (App de VideoJuegos)");

        log.info("Servidor enviá: {}", message);

        session.sendMessage(message);

    }
```
### 2. Método afterConnectionClosed
Este método se ejecuta cuando la conexión del cliente con el WebSocket se cierra. Su función es eliminar la sesión del conjunto de sesiones activas y registrar el cierre.
```java
@Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Conexión cerrada con el servidor: {}",status);
        sessions.remove(session);
    }
```
### 3. Método sendMessage
Este método envía un mensaje a todas las sesiones activas del WebSocket. Primero verifica que la sesión esté abierta y luego envía el mensaje correspondiente, registrando la acción en los logs.
```java
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
```
### 4. Método sendPeriodicMessages
Este método envía mensajes periódicos automáticos a todas las sesiones abiertas. Se ejecuta cada segundo (según @Scheduled(fixedRate = 1000)) y puede usarse para mantener al cliente actualizado con información del servidor.
```java
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
```

### 5. Método handleTextMessage
Se ejecuta cuando el WebSocket recibe un mensaje de texto del cliente. Su función es leer el mensaje recibido, generar una respuesta y enviarla de vuelta. También registra en los logs el mensaje recibido y la respuesta enviada.
```java
@Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();

        log.info("Server received: " + request);
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));

        log.info("Server sends: " + response);
        session.sendMessage(new TextMessage(response));
    }
```

### 6. Método handleTransportError
Este método se ejecuta cuando ocurre un error de transporte en la comunicación WebSocket, como problemas de red. Su función principal es registrar el error para poder diagnosticar problemas.
```java
@Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: {}" , exception.getMessage());
    }
```
### 7. Método getSubProtocols
Este método devuelve una lista de protocolos subyacentes compatibles que el WebSocket puede manejar. Es útil para asegurarse de que cliente y servidor usen un protocolo común.
```java
@Override
    public List<String> getSubProtocols() {
        return List.of("supprotocol.demo.websocket");
    }
```
## Lo siguiente que hacemos es modificar el servicio de la entidad videoJuegos.
Lo primero es implementar `InitializingBean`, que es una interfaz de Spring que permite ejecutar código **después de que todas las dependencias del bean hayan sido inyectadas**.

```java
public class VideoJuegosServiceImpl implements VideoJuegoService, InitializingBean {
```
y que tiene un solo método:
```java
public void afterPropertiesSet(){
    this.webSocketService = this.webSocketConfig.webSocketVideoJuegosHandler();
}
```
### Para probar los test tenemos este método:
```java
public void setWebSocketService(WebSocketHandler webSocketHandler) {
        this.webSocketService = webSocketHandler;
    }
```
Este método se usa para inyectar un WebSocket “falso” (mock o spy) en los tests, de manera que podamos probar que el servicio llama a los métodos del WebSocket sin necesidad de tener un WebSocket real funcionando.

### Método onChange
```java
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
```
Este método se ejecuta cada vez que ocurre un cambio en un videojuego (creación, actualización o eliminación). Su función es preparar y enviar una notificación a todos los clientes conectados por WebSocket.

### *VideoJuegosServiceImplTest*.
Lo próximo que vemos es el test del servicio de videoJuegos y añadimos los mock necesarios como:
```java
@Mock
private WebSocketConfig webSocketConfig; // Simula la configuración de WebSocket de Spring

@Mock
private VideoJuegosNotificationMapper videoJuegosNotificationMapper; // Simula el mapeo de VideoJuegos a DTO de notificación

@Mock
private ObjectMapper objectMapper; // Simula la conversión de objetos a JSON

@Mock
private WebSocketHandler webSocketService; // Simula el WebSocket que envía mensajes a los clientes

@BeforeEach
void setUp() {
    videoJuegosResponse1 = videoJuegosMapper.toVideoJuegosResponseDto(videoJuegos1);
    juegosService.setWebSocketService(webSocketService); // Inyecta el mock para probar los envíos de mensajes
}
```
### Los usaremos únicamente para los test de crear, actualizar y borrar.
#### Crear:
Comprueba que el servicio puede guardar un videojuego correctamente y que se envía la notificación a los clientes vía WebSocket.
```java
@Test
    void saveVideoJuegosConValidosParametros() throws IOException {

        log.info("Guardando Videojuego con parametros validos");

        VideoJuegosCreateDto videoJuegosCreateDto = VideoJuegosCreateDto.builder()

                .cliente("Pedro")
                .nombre("FC 26")
                .precio(100.0)
                .fecha_lanzamiento(LocalDate.of(2026, 9, 19))
                .genero("DEPORTES")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(3)
                .build();

        VideoJuegos expectedVideoJuegos = VideoJuegos.builder()
                .id(1L)
                .cliente(cliente3)
                .nombre("FC 26")
                .precio(100.0)
                .fecha_lanzamiento(LocalDate.of(2026, 9, 19))
                .genero("Deportes")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(3)
                .build();

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosMapper.toVideoJuegosResponseDto(expectedVideoJuegos);
        when(clienteService.findByNombre(videoJuegosCreateDto.getCliente())).thenReturn(cliente3);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(expectedVideoJuegos);
        doNothing().when(webSocketService).sendMessage(any());

        VideoJuegosResponseDto actualVideoJuegosResponse = juegosService.save(videoJuegosCreateDto);

        assertEquals(expectedVideoJuegosResponse, actualVideoJuegosResponse);

        verify(juegosRepository).save(videoJuegosCaptor.capture());

        VideoJuegos capturedVideoJuegos = videoJuegosCaptor.getValue();

        assertEquals(expectedVideoJuegos.getNombre(), capturedVideoJuegos.getNombre());
    }
```

### Update:
Comprueba que el servicio puede actualizar un videojuego existente correctamente y que se envía la notificación a los clientes vía WebSocket.
```java
@Test
    void update_VideoJuego_ConIdValida() throws IOException {
        log.info("Actualizando Videojuego con parametro valido");

        Long id = 2L;

        String nombre = "GTA VI - Edición Especial";

        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegos1));

        VideoJuegosUpdateDto videoJuegosUpdateDto = VideoJuegosUpdateDto.builder()
                .nombre(nombre)
                .build();

        VideoJuegos videoJuegoActualizado = videoJuegosMapper.toVideoJuegosUpdate(videoJuegosUpdateDto, videoJuegos1);
        when(juegosRepository.save(any(VideoJuegos.class))).thenReturn(videoJuegoActualizado);

        videoJuegosResponse1.setNombre(nombre);

        VideoJuegosResponseDto expectedVideoJuegosResponse = videoJuegosResponse1;
        doNothing().when(webSocketService).sendMessage(any());

        VideoJuegosResponseDto actualVideoJuegosResponse = juegosService.update(id, videoJuegosUpdateDto);

        assertThat(actualVideoJuegosResponse)
        .usingRecursiveComparison()
                .ignoringFields("fecha_lanzamiento", "precio", "genero", "plataforma", "edad")
                .isEqualTo(expectedVideoJuegosResponse);

        verify(juegosRepository).findById(id);
        verify(juegosRepository).save(any());
    }
```
### Delete:
Verifica que el servicio puede eliminar un videojuego existente y que no lanza errores al hacerlo.
```java
@Test
    void deleteByIdConParametroValido() throws IOException {

        log.info("Eliminando Videojuego con parametro valido");

        long id = 7L;

        VideoJuegos videoJuegosAEliminar = VideoJuegos.builder()
                .id(id)
                .cliente(cliente1)
                .nombre("GTA VI")
                .precio(120.0)
                .fecha_lanzamiento(LocalDate.of(2026, 5, 26))
                .genero("Acción")
                .plataforma(VideoJuegos.Plataforma.PS5)
                .edad(18)
                .build();

        when(juegosRepository.findById(id)).thenReturn(Optional.of(videoJuegosAEliminar));
        doNothing().when(webSocketService).sendMessage(any());

        assertThatCode(() -> juegosService.deleteById(id))
                .doesNotThrowAnyException();

        verify(juegosRepository).deleteById(id);
    }
```