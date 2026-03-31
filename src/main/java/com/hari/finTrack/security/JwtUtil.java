package com.hari.finTrack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
 * ==================== UTILIDAD JWT ====================
 *
 * JWT (JSON Web Token) es un estándar para transmitir información de forma segura
 * entre cliente y servidor como un string firmado.
 *
 * Estructura de un token JWT: HEADER.PAYLOAD.SIGNATURE
 *   - Header:    algoritmo de firma (HS256)
 *   - Payload:   datos del usuario (email, userId, fecha expiración)
 *   - Signature: firma digital con nuestra clave secreta
 *
 * El token se genera al hacer login/registro y contiene:
 *   - subject: email del usuario
 *   - userId: ID del usuario en la BD
 *   - issuedAt: cuándo se creó
 *   - expiration: cuándo expira (24h por defecto)
 *
 * Configuración en application.properties:
 *   jwt.secret=<clave secreta de al menos 256 bits para HS256>
 *   jwt.expiration-ms=86400000  (24 horas en milisegundos)
 */
@Component
public class JwtUtil {

	private final SecretKey key;
	private final long expirationMs;

	public JwtUtil(@Value("${jwt.secret}") String secret,
				   @Value("${jwt.expiration-ms}") long expirationMs) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	// Genera un token JWT con el userId y email del usuario.
	// Este token es el que el cliente guarda y envía en cada petición.
	public String generateToken(Long userId, String email) {
		return Jwts.builder()
				.subject(email)
				.claim("userId", userId)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(key)
				.compact();
	}

	public String getEmailFromToken(String token) {
		return parseClaims(token).getSubject();
	}

	public Long getUserIdFromToken(String token) {
		return parseClaims(token).get("userId", Long.class);
	}

	// Valida que el token no esté expirado y que la firma sea correcta.
	// Si alguien modifica el payload, la firma no coincidirá y devolverá false.
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
