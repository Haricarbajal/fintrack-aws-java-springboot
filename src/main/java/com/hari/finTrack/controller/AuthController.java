package com.hari.finTrack.controller;

import com.hari.finTrack.dto.AuthResponse;
import com.hari.finTrack.dto.LoginRequest;
import com.hari.finTrack.dto.RegisterRequest;
import com.hari.finTrack.model.User;
import com.hari.finTrack.repository.UserRepository;
import com.hari.finTrack.security.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * ==================== CONTROLADOR DE AUTENTICACIÓN ====================
 *
 * Endpoints PÚBLICOS (no requieren token):
 *
 *   POST /api/auth/register  → Registrar un usuario nuevo
 *   POST /api/auth/login     → Iniciar sesión y obtener token JWT
 *
 * FLUJO:
 *   1. El cliente envía email + password (y nombre en registro)
 *   2. El servidor valida los datos
 *   3. Si es correcto, devuelve un token JWT que el cliente debe guardar
 *   4. Ese token se usa en TODAS las peticiones posteriores como:
 *        Header → Authorization: Bearer <token>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(UserRepository userRepository,
						  PasswordEncoder passwordEncoder,
						  JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	/*
	 * POST /api/auth/register
	 *
	 * Registra un nuevo usuario en el sistema.
	 *
	 * Request body (JSON):
	 *   {
	 *     "email": "juan@email.com",
	 *     "nombre": "Juan Garcia",
	 *     "password": "miPassword123"
	 *   }
	 *
	 * Respuestas:
	 *   201 CREATED  → Registro exitoso, devuelve { token, email, nombre }
	 *   409 CONFLICT → Ya existe un usuario con ese email
	 *
	 * La password se guarda encriptada con BCrypt (nunca en texto plano).
	 * El token devuelto sirve para autenticarse inmediatamente sin hacer login.
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register( @Valid @RequestBody RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Ya existe un usuario con ese email");
		}

		User user = new User();
		user.setEmail(request.email());
		user.setNombre(request.nombre());
		user.setPassword(passwordEncoder.encode(request.password()));
		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getId(), user.getEmail());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new AuthResponse(token, user.getEmail(), user.getNombre()));
	}

	/*
	 * POST /api/auth/login
	 *
	 * Inicia sesión con un usuario existente.
	 *
	 * Request body (JSON):
	 *   {
	 *     "email": "juan@email.com",
	 *     "password": "miPassword123"
	 *   }
	 *
	 * Respuestas:
	 *   200 OK           → Login exitoso, devuelve { token, email, nombre }
	 *   401 UNAUTHORIZED → Email o password incorrectos
	 *
	 * El token JWT devuelto expira en 24 horas (configurable en application.properties).
	 * El cliente debe guardarlo (localStorage, sessionStorage, etc.) y enviarlo
	 * en el header Authorization de cada petición protegida.
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login( @Valid @RequestBody LoginRequest request) {
		return userRepository.findByEmail(request.email())
				.filter(user -> passwordEncoder.matches(request.password(), user.getPassword()))
				.map(user -> {
					String token = jwtUtil.generateToken(user.getId(), user.getEmail());
					return ResponseEntity.ok((Object) new AuthResponse(token, user.getEmail(), user.getNombre()));
				})
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas"));
	}
}
