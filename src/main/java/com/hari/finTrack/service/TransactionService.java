package com.hari.finTrack.service;

import com.hari.finTrack.model.Transaction;
import com.hari.finTrack.model.User;
import com.hari.finTrack.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * ==================== SERVICIO DE TRANSACCIONES ====================
 *
 * Capa de lógica de negocio para transacciones.
 * TODAS las operaciones reciben el User como parámetro para garantizar
 * que un usuario solo pueda operar con SUS propias transacciones.
 *
 * El Controller obtiene el User del token JWT y lo pasa aquí.
 * El Repository filtra las queries por user_id en la base de datos.
 *
 * @Transactional: garantiza que si algo falla a mitad de la operación,
 * los cambios se revierten (rollback automático).
 */
@Service
public class TransactionService {

	private final TransactionRepository transactionRepository;

	public TransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	// Devuelve todas las transacciones del usuario (solo las suyas)
	public List<Transaction> findAllByUser(User user) {
		return transactionRepository.findByUser(user);
	}

	// Busca una transacción por ID, pero solo si pertenece al usuario.
	// Si el usuario A intenta ver la transacción del usuario B → lanza excepción → 404
	public Transaction findByIdAndUser(Long id, User user) {
		return transactionRepository.findByIdAndUser(id, user)
				.orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada: " + id));
	}

	// Crea una transacción nueva y la asocia automáticamente al usuario autenticado.
	// Se pone id=null para evitar que el cliente intente forzar un ID.
	@Transactional
	public Transaction create(Transaction transaction, User user) {
		transaction.setId(null);
		transaction.setUser(user);
		return transactionRepository.save(transaction);
	}

	// Actualiza todos los campos de una transacción existente del usuario.
	// Primero verifica que la transacción exista Y pertenezca al usuario.
	@Transactional
	public Transaction update(Long id, Transaction datos, User user) {
		Transaction existente = findByIdAndUser(id, user);
		existente.setDescripcion(datos.getDescripcion());
		existente.setMonto(datos.getMonto());
		existente.setTipo(datos.getTipo());
		existente.setFecha(datos.getFecha());
		existente.setCategoria(datos.getCategoria());
		return transactionRepository.save(existente);
	}

	// Elimina una transacción solo si existe y pertenece al usuario.
	@Transactional
	public void deleteByIdAndUser(Long id, User user) {
		if (!transactionRepository.existsByIdAndUser(id, user)) {
			throw new IllegalArgumentException("Transacción no encontrada: " + id);
		}
		transactionRepository.deleteById(id);
	}
}
