package com.example.aplicacionvideojuegos.rest.auth.services.authentication;

import com.example.aplicacionvideojuegos.rest.auth.dto.JwtAuthResponse;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignInRequest;
import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {

    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
