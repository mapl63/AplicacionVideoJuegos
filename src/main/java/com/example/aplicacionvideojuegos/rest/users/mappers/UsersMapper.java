package com.example.aplicacionvideojuegos.rest.users.mappers;

import com.example.aplicacionvideojuegos.rest.users.dto.UserInfoResponse;
import com.example.aplicacionvideojuegos.rest.users.dto.UserRequest;
import com.example.aplicacionvideojuegos.rest.users.dto.UserResponse;
import com.example.aplicacionvideojuegos.rest.users.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {

    public User toUserCreated(UserRequest userRequest) {
        return User.builder()
                .nombre(userRequest.getNombre())
                .apellidos(userRequest.getApellidos())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .isDeleted(userRequest.isDeleted())
                .build();
    }

    public User toUserUpdated(UserRequest userRequest, Long id) {
        return User.builder()
                .id(id)
                .nombre(userRequest.getNombre())
                .apellidos(userRequest.getApellidos())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .isDeleted(userRequest.isDeleted())
                .build();
    }

    public UserResponse toUserRsponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellidos(user.getApellidos())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    public UserInfoResponse toUserInfoResponse(User user, List<String> videoJuegos){
       return UserInfoResponse.builder()
               .id(user.getId())
               .nombre(user.getNombre())
               .apellidos(user.getApellidos())
               .username(user.getUsername())
               .email(user.getEmail())
               .roles(user.getRoles())
               .isDeleted(user.getIsDeleted())
               .videoJuegos(videoJuegos)
               .build();
    }
}
