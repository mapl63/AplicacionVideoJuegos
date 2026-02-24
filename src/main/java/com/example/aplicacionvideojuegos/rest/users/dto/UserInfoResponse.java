package com.example.aplicacionvideojuegos.rest.users.dto;


import com.example.aplicacionvideojuegos.rest.users.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.*;
import java.util.Set;

// ======================================================
// 🔵 DTO UserInfoResponse
// ======================================================
//Mostrar usuario + información adicional
// ======================================================

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;

    private String nombre;

    private String apellidos;

    private String username;

    private String email;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    private List<String> videoJuegos = new ArrayList<>();
}
