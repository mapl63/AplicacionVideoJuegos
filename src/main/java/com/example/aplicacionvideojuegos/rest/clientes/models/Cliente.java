package com.example.aplicacionvideojuegos.rest.clientes.models;


import com.example.aplicacionvideojuegos.rest.users.models.User;
import com.example.aplicacionvideojuegos.rest.videoJuegos.models.VideoJuegos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name = "CLIENTES")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false, length = 100)
    private String nombre;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "cliente")
    @JsonIgnoreProperties("cliente")
    private List<VideoJuegos> videoJuegos;

    @OneToOne(mappedBy = "cliente")
    @JsonIgnoreProperties("cliente")
    private User usuario;

}
