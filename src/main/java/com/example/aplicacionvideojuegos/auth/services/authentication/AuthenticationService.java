package com.example.aplicacionvideojuegos.auth.services.authentication;

import com.example.aplicacionvideojuegos.auth.dto.JwtAuthResponse;
import com.example.aplicacionvideojuegos.auth.dto.UserSignInRequest;
import com.example.aplicacionvideojuegos.auth.dto.UserSignUpRequest;

public interface AuthenticationService {

    JwtAuthResponse singUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
