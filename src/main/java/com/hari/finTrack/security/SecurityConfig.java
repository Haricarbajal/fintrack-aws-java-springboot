package com.hari.finTrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * ==================== CONFIGURACIÓN DE SEGURIDAD ====================
 *
 * Define las reglas de seguridad de toda la aplicación:
 *
 *   - CSRF desactivado: porque usamos JWT (tokens stateless), no cookies de sesión.
 *     CSRF protege contra ataques basados en cookies, que no aplican aquí.
 *
 *   - Sesiones STATELESS: el servidor NO guarda estado de sesión.
 *     Cada petición se autentica por sí sola a través del token JWT.
 *
 *   - Rutas públicas: /api/auth/** (login y registro) → cualquiera puede acceder
 *   - Rutas protegidas: todo lo demás → requiere token JWT válido en el header
 *
 *   - JwtAuthenticationFilter se ejecuta ANTES del filtro de Spring Security.
 *     Si encuentra un token válido, autentica al usuario automáticamente.
 *
 * FLUJO DE UNA PETICIÓN PROTEGIDA:
 *   1. Llega petición con header "Authorization: Bearer <token>"
 *   2. JwtAuthenticationFilter intercepta, valida el token, extrae userId/email
 *   3. Si es válido → crea un UserPrincipal y lo pone en el SecurityContext
 *   4. Spring Security ve que hay autenticación → permite pasar al Controller
 *   5. Si no hay token o es inválido → Spring Security devuelve 401/403
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// BCrypt: algoritmo de hash para passwords. Genera un hash diferente cada vez
	// (usa salt interno), así que dos usuarios con la misma password tendrán hashes distintos.
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
