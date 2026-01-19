package com.example.aplicacionvideojuegos.web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${spring.application.name}")
    private String appName;

    @ModelAttribute("appName")
    public String getAppName() {
        return appName;
    }

    @ModelAttribute("currentYear")
    public int getCurrentYear() {
        return LocalDate.now().getYear();
    }

}
