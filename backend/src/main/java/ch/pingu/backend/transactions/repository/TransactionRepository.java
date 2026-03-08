package ch.pingu.backend.transactions.repository;

import ch.pingu.backend.transactions.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByConsultantIdOrderByCreatedAtDesc(String consultantId);
    List<Transaction> findAllByOrderByCreatedAtDesc();
}
