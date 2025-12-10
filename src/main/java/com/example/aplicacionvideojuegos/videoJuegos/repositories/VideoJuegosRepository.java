package com.example.aplicacionvideojuegos.videoJuegos.repositories;


import com.example.aplicacionvideojuegos.videoJuegos.models.VideoJuegos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface VideoJuegosRepository extends JpaRepository<VideoJuegos, Long> , JpaSpecificationExecutor<VideoJuegos> {

    List<VideoJuegos> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE VideoJuegos v SET v.isDeleted = true WHERE v.id = :id")
    void updateIsDeletedToTrueById(Long id);

}
