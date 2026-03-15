package ch.pingu.backend.transactions.service;

import ch.pingu.backend.transactions.model.Transaction;
import ch.pingu.backend.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> listAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Transaction> findById(String id) {
        return repository.findById(id);
    }

    public List<Transaction> findByConsultantId(String consultantId) {
        return repository.findByConsultantIdOrderByCreatedAtDesc(consultantId);
    }

    public Transaction create(Transaction txn) {
        if (txn.getId() == null || txn.getId().isBlank()) {
            txn.setId(UUID.randomUUID().toString());
        }
        if (txn.getCreatedAt() == null) {
            txn.setCreatedAt(LocalDateTime.now());
        }
        if (txn.getExecutionDate() == null) {
            txn.setExecutionDate(LocalDateTime.now());
        }
        if (txn.getStatus() == null) {
            txn.setStatus("NOT_STARTED");
        }
        return repository.save(txn);
    }

    public Transaction revert(String id, String reason, String revertedBy) {
        Transaction txn = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found: " + id));
        txn.setStatus("REVERTED");
        txn.setRevertReason(reason);
        txn.setRevertedAt(LocalDateTime.now());
        txn.setRevertedBy(revertedBy);
        return repository.save(txn);
    }
}
