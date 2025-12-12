package com.example.aplicacionvideojuegos.users.services;

import com.example.aplicacionvideojuegos.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.users.dto.UserResponse;

import com.example.aplicacionvideojuegos.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Page<UserResponse> findAll(
            Optional<String> username,
            Optional<String> email,
            Optional<Boolean> isDeleted,
            Pageable pageable
    );

    UserInfoResponse findById(Long id);

    UserResponse save(UserRequest userRequest);

    UserResponse update(Long id, UserRequest userRequest);

    void deleteById(Long id);

    List<User> findAllActiveUsers();

}
