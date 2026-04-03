package com.hari.finTrack.controller;

import com.hari.finTrack.exception.ResourceNotFoundException;
import com.hari.finTrack.model.Transaction;
import com.hari.finTrack.model.User;
import com.hari.finTrack.repository.UserRepository;
import com.hari.finTrack.security.UserPrincipal;
import com.hari.finTrack.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * ==================== CONTROLADOR DE TRANSACCIONES ====================
 *
 * Endpoints PROTEGIDOS (requieren header: Authorization: Bearer <token>):
 *
 *   GET    /api/transactions      → Listar TODAS las transacciones del usuario autenticado
 *   GET    /api/transactions/{id} → Obtener UNA transacción por ID (solo si es del usuario)
 *   POST   /api/transactions      → Crear una nueva transacción
 *   PUT    /api/transactions/{id} → Actualizar una transacción existente
 *   DELETE /api/transactions/{id} → Eliminar una transacción
 *
 * SEGURIDAD:
 *   - Cada usuario solo puede ver/editar/eliminar SUS PROPIAS transacciones.
 *   - Si un usuario intenta acceder a una transacción de otro usuario, recibe 404.
 *   - @AuthenticationPrincipal inyecta automáticamente los datos del usuario
 *     que viene en el token JWT (userId + email).
 *
 * EJEMPLO DE LLAMADA (con curl):
 *   curl -X GET http://localhost:8080/api/transactions \
 *     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	private final TransactionService transactionService;
	private final UserRepository userRepository;

	public TransactionController(TransactionService transactionService,
								 UserRepository userRepository) {
		this.transactionService = transactionService;
		this.userRepository = userRepository;
	}

	// Obtiene la entidad User completa a partir del UserPrincipal que viene del token JWT.
	private User getUser(UserPrincipal principal) {
		return userRepository.findById(principal.userId())
				.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

	}

	/*
	 * GET /api/transactions
	 * Header: Authorization: Bearer <token>
	 *
	 * Devuelve TODAS las transacciones del usuario autenticado.
	 * Respuesta: 200 OK → [ { id, descripcion, monto, tipo, fecha, categoria }, ... ]
	 * Si no tiene transacciones, devuelve una lista vacía [].
	 */
	@GetMapping
	public List<Transaction> listar(@AuthenticationPrincipal UserPrincipal principal) {
		return transactionService.findAllByUser(getUser(principal));
	}

	/*
	 * GET /api/transactions/{id}
	 * Header: Authorization: Bearer <token>
	 *
	 * Devuelve UNA transacción por su ID, solo si pertenece al usuario autenticado.
	 * Respuestas:
	 *   200 OK       → { id, descripcion, monto, tipo, fecha, categoria }
	 *   404 NOT FOUND → La transacción no existe o pertenece a otro usuario
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Transaction> obtener(@PathVariable Long id,
											   @AuthenticationPrincipal UserPrincipal principal) {
		return ResponseEntity.ok(transactionService.findByIdAndUser(id, getUser(principal)));
	}

	/*
	 * POST /api/transactions
	 * Header: Authorization: Bearer <token>
	 *
	 * Crea una nueva transacción asociada al usuario autenticado.
	 *
	 * Request body (JSON):
	 *   {
	 *     "descripcion": "Compra supermercado",
	 *     "monto": 45.50,
	 *     "tipo": "GASTO",            ← valores posibles: "GASTO" o "INGRESO"
	 *     "fecha": "2026-03-29",       ← formato: YYYY-MM-DD
	 *     "categoria": "Alimentacion"
	 *   }
	 *
	 * Respuesta: 201 CREATED → { id, descripcion, monto, tipo, fecha, categoria }
	 * El campo "user" NO se envía en el body; se asigna automáticamente del token.
	 */
	@PostMapping
	public ResponseEntity<Transaction> crear(@Valid @RequestBody Transaction transaction,
											 @AuthenticationPrincipal UserPrincipal principal) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(transactionService.create(transaction, getUser(principal)));
	}

	/*
	 * PUT /api/transactions/{id}
	 * Header: Authorization: Bearer <token>
	 *
	 * Actualiza TODOS los campos de una transacción existente del usuario.
	 *
	 * Request body (JSON): misma estructura que POST
	 * Respuestas:
	 *   200 OK        → Transacción actualizada
	 *   404 NOT FOUND → No existe o pertenece a otro usuario
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Transaction> actualizar(@PathVariable Long id,
												  @Valid @RequestBody Transaction transaction,
												  @AuthenticationPrincipal UserPrincipal principal) {
		return ResponseEntity.ok(transactionService.update(id, transaction, getUser(principal)));
	}

	/*
	 * DELETE /api/transactions/{id}
	 * Header: Authorization: Bearer <token>
	 *
	 * Elimina una transacción por ID, solo si pertenece al usuario autenticado.
	 * Respuestas:
	 *   204 NO CONTENT → Eliminada correctamente (sin body)
	 *   404 NOT FOUND  → No existe o pertenece a otro usuario
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id,
										@AuthenticationPrincipal UserPrincipal principal) {
		transactionService.deleteByIdAndUser(id, getUser(principal));
		return ResponseEntity.noContent().build();
	}
}
