package com.example.aplicacionvideojuegos.rest.users.services;

import com.example.aplicacionvideojuegos.rest.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.rest.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.rest.users.dto.UserResponse;

import com.example.aplicacionvideojuegos.rest.users.models.User;
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
