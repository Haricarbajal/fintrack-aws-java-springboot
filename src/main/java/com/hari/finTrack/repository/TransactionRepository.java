package com.hari.finTrack.repository;

import com.hari.finTrack.model.Transaction;
import com.hari.finTrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByUser(User user);

	Optional<Transaction> findByIdAndUser(Long id, User user);

	boolean existsByIdAndUser(Long id, User user);
}
