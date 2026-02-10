package com.example.aplicacionvideojuegos.web.controllers;


import com.example.aplicacionvideojuegos.rest.auth.dto.UserSignUpRequest;
import com.example.aplicacionvideojuegos.rest.auth.services.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegisterWebController {

    private final AuthenticationService authenticationService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserSignUpRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("user") UserSignUpRequest request,
            BindingResult result,
            RedirectAttributes redirect
    ){
        if(result.hasErrors()){
            return "auth/register";
        }

        try {
            authenticationService.singUp(request);
            redirect.addFlashAttribute("success", "Usuario registrado exitosamente");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }
}