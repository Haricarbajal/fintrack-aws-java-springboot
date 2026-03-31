package com.hari.finTrack.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * ==================== FILTRO JWT ====================
 *
 * Este filtro se ejecuta en CADA petición HTTP antes de llegar al Controller.
 * Es el encargado de leer el token JWT del header y autenticar al usuario.
 *
 * FLUJO:
 *   1. Lee el header "Authorization" de la petición
 *   2. Si tiene formato "Bearer <token>":
 *      a. Valida que el token no esté expirado ni manipulado
 *      b. Extrae el email y userId del token
 *      c. Crea un UserPrincipal con esos datos
 *      d. Lo registra en el SecurityContext de Spring Security
 *   3. Si no hay header o el token es inválido → no hace nada (la petición sigue
 *      sin autenticación y Spring Security la rechazará si la ruta es protegida)
 *
 * OncePerRequestFilter garantiza que este filtro se ejecuta UNA sola vez por petición,
 * incluso si hay redirects internos.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		// 1. Buscar el header "Authorization: Bearer <token>"
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			// 2. Extraer el token (quitar "Bearer ")
			String token = header.substring(7);

			if (jwtUtil.validateToken(token)) {
				// 3. Extraer datos del usuario desde el token
				String email = jwtUtil.getEmailFromToken(token);
				Long userId = jwtUtil.getUserIdFromToken(token);

				// 4. Crear el principal y registrarlo en el contexto de seguridad
				// Esto es lo que permite usar @AuthenticationPrincipal en los Controllers
				UserPrincipal principal = new UserPrincipal(userId, email);
				UsernamePasswordAuthenticationToken auth =
						new UsernamePasswordAuthenticationToken(principal, null, List.of());
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}

		// 5. Continuar con la cadena de filtros (siempre, autenticado o no)
		filterChain.doFilter(request, response);
	}
}
