package com.hari.finTrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO para el body de POST /api/auth/login
// Ejemplo JSON: { "email": "juan@email.com", "password": "123456" }
public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
}