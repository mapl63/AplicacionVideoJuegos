package com.example.aplicacionvideojuegos.rest.videoJuegos.models;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "VIDEOJUEGOS")
@Schema(name = "videoJuegos")
public class VideoJuegos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del videojuego", example = "1")
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "cliente_id")
    @Schema(description = "Cliente asociado al videojuego", example = "Jeromito")
    private Cliente cliente;

    @Column(nullable = false, length = 55)
    @Schema(description = "Nombre del videojuego", example = "The Last of Us Part II")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Precio del videojuego", example = "59.99")
    private Double precio;

    @Column(nullable = false)
    @Schema(description = "Fecha de lanzamiento del videojuego", example = "2020-06-19")
    private LocalDate fecha_lanzamiento;

    @Column(nullable = false, length = 50)
    @Schema(description = "Género del videojuego", example = "Acción")
    private String genero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Plataforma del videojuego", example = "PS4")
    private Plataforma  plataforma;

    @Column(nullable = false)
    @Schema(description = "Edad recomendada para el videojuego", example = "18")
    private Integer edad;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    public enum Plataforma {
        PS4, PS5, PC, XBOXONE, NINTENDO
    }

}
