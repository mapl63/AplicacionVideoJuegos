package com.example.aplicacionvideojuegos.rest.clientes.repositories;

import com.example.aplicacionvideojuegos.rest.clientes.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.Optional;



@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {


    Optional<Cliente> findByNombreEqualsIgnoreCase(String nombre);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);


    List<Cliente> findByIsDeleted(Boolean isDeleted);

    @Modifying
    @Query("UPDATE Cliente c SET c.isDeleted = true WHERE c.id = :id")
    void updateIsDeleteToTrueById(Long id);

    @Query("SELECT CASE WHEN COUNT(vj) > 0 THEN true ELSE false END FROM VideoJuegos vj WHERE vj.cliente.id = :id")
    Boolean existsVideoJuegoById(Long id);
}
