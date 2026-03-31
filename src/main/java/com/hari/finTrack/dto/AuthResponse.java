package com.hari.finTrack.dto;

// DTO de respuesta para login y registro.
// Ejemplo JSON: { "token": "eyJhbG...", "email": "juan@email.com", "nombre": "Juan Garcia" }
// El cliente debe guardar el "token" y enviarlo en el header Authorization de cada petición.
public record AuthResponse(String token, String email, String nombre) {
}
