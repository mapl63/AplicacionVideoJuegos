package com.example.aplicacionvideojuegos.users.repositories;

import com.example.aplicacionvideojuegos.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    @Modifying
    @Query("update User p set p.isDeleted = true where p.id = :id")
    void updateIsDeleteToTrueById(Long id);

    List<User> findAllByIsDeletedFalse();

}
