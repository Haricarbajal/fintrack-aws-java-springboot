package com.hari.finTrack.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// DTO (Data Transfer Object) para el body de POST /api/auth/register
// Ejemplo JSON: { "email": "juan@email.com", "nombre": "Juan Garcia", "password": "123456" }

public record RegisterRequest(@Email @NotBlank String email,@NotBlank String nombre,@NotBlank @Size(min = 8) String password){
}