package com.example.aplicacionvideojuegos.videoJuegos.repositories;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface VideoJuegosRepository extends JpaRepository<VideoJuegos, Long> {



    List<VideoJuegos> findByNombre(String nombre);
    //List<VideoJuegos> findByNombreAndIsDeletedFalse(String nombre);

    @Query("SELECT v FROM VideoJuegos v WHERE LOWER(v.cliente.nombre) LIKE %:cliente%")
    List<VideoJuegos> findByClienteContainsIgnoreCase(String cliente);

    //List<VideoJuegos> findByClienteContainsIgnoreCaseAndIsDeletedFalse(String cliente);

    //Busqueda por nombre y cliente ignorando mayusculas y minusculas
    @Query("SELECT v FROM VideoJuegos v " +
            "WHERE LOWER(v.nombre) = LOWER(:nombre) " +
            "AND LOWER(v.cliente.nombre) LIKE %:cliente%")
    List<VideoJuegos> findByNombreAndClienteContainsIgnoreCase(String nombre, String cliente);

    //List<VideoJuegos> findByNombreAndClienteContainsIgnoreCaseAndIsDeletedFalse(String nombre, String cliente);

    List<VideoJuegos> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE VideoJuegos v SET v.isDeleted = true WHERE v.id = :id")
    void updateIsDeletedToTrueById(Long id);

}
