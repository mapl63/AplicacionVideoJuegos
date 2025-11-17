package com.example.aplicacionvideojuegos.videoJuegos.models;

import com.example.aplicacionvideojuegos.clientes.models.Cliente;
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
public class VideoJuegos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 55)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private LocalDate fecha_lanzamiento;

    @Column(nullable = false, length = 50)
    private String genero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plataforma  plataforma;

    @Column(nullable = false)
    private Integer edad;

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    public enum Plataforma {
        PS4, PS5, PC, XBOXONE, NINTENDO
    }

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
