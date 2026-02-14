package ch.pingu.domain.service;

import ch.pingu.domain.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain service for transaction operations
 */
public class TransactionService {
    
    /**
     * Create a new transaction
     */
    public Transaction createTransaction(String consultantId, String customerId,
                                        Money sourceAmount, Money targetAmount,
                                        double exchangeRate, String exchangeRateVersionId,
                                        LocalDateTime executionDate, String createdBy) {
        return Transaction.create(
            consultantId,
            customerId,
            sourceAmount,
            targetAmount,
            exchangeRate,
            exchangeRateVersionId,
            executionDate,
            createdBy
        );
    }
    
    /**
     * Filter transactions by consultant (for consultants viewing their own transactions)
     */
    public List<Transaction> filterByConsultant(List<Transaction> transactions, String consultantId) {
        return transactions.stream()
            .filter(t -> t.getConsultantId().equals(consultantId))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter transactions by status
     */
    public List<Transaction> filterByStatus(List<Transaction> transactions, TransactionStatus status) {
        return transactions.stream()
            .filter(t -> t.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter transactions by currency (source or target)
     */
    public List<Transaction> filterByCurrency(List<Transaction> transactions, Currency currency) {
        return transactions.stream()
            .filter(t -> t.getSourceAmount().getCurrency() == currency || 
                        t.getTargetAmount().getCurrency() == currency)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter transactions by date range
     */
    public List<Transaction> filterByDateRange(List<Transaction> transactions, 
                                               LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
            .filter(t -> {
                LocalDate execDate = t.getExecutionDate().toLocalDate();
                return !execDate.isBefore(startDate) && !execDate.isAfter(endDate);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Filter transactions by customer
     */
    public List<Transaction> filterByCustomer(List<Transaction> transactions, String customerId) {
        return transactions.stream()
            .filter(t -> t.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a user can revert a transaction (only admins)
     */
    public boolean canRevertTransaction(User user) {
        return user.isAdmin();
    }
    
    /**
     * Revert a transaction (admin only)
     */
    public void revertTransaction(Transaction transaction, String reason, User admin) {
        if (!canRevertTransaction(admin)) {
            throw new SecurityException("Only administrators can revert transactions");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Revert reason is required");
        }
        
        transaction.revert(reason, admin.getUsername());
    }
}
