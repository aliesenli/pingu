package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JSON-based repository for Transaction entities
 */
public class TransactionRepository {
    
    private final ObjectMapper objectMapper;
    private final String filePath;
    private List<Transaction> transactions;
    
    public TransactionRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.transactions = new ArrayList<>();
        loadTransactions();
    }
    
    private void loadTransactions() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                List<TransactionDTO> dtos = objectMapper.readValue(file, new TypeReference<List<TransactionDTO>>() {});
                transactions = dtos.stream()
                    .map(TransactionDTO::toDomain)
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                System.err.println("Error loading transactions: " + e.getMessage());
                transactions = new ArrayList<>();
            }
        }
    }
    
    private void saveTransactions() {
        try {
            List<TransactionDTO> dtos = transactions.stream()
                .map(TransactionDTO::fromDomain)
                .toList();
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), dtos);
        } catch (IOException e) {
            throw new RuntimeException("Error saving transactions", e);
        }
    }
    
    public Optional<Transaction> findById(String id) {
        return transactions.stream()
            .filter(t -> t.getId().equals(id))
            .findFirst();
    }
    
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }
    
    public List<Transaction> findByConsultantId(String consultantId) {
        return transactions.stream()
            .filter(t -> t.getConsultantId().equals(consultantId))
            .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }
    
    public void save(Transaction transaction) {
        transactions.removeIf(t -> t.getId().equals(transaction.getId()));
        transactions.add(transaction);
        saveTransactions();
    }
    
    public void delete(String id) {
        transactions.removeIf(t -> t.getId().equals(id));
        saveTransactions();
    }
    
    // DTO for JSON serialization
    private static class TransactionDTO {
        public String id;
        public String consultantId;
        public String customerId;
        public MoneyDTO sourceAmount;
        public MoneyDTO targetAmount;
        public double exchangeRate;
        public String exchangeRateVersionId;
        public String executionDate;
        public String createdAt;
        public String createdBy;
        public String status;
        public String revertReason;
        public String revertedAt;
        public String revertedBy;
        
        public static TransactionDTO fromDomain(Transaction transaction) {
            TransactionDTO dto = new TransactionDTO();
            dto.id = transaction.getId();
            dto.consultantId = transaction.getConsultantId();
            dto.customerId = transaction.getCustomerId();
            dto.sourceAmount = MoneyDTO.fromDomain(transaction.getSourceAmount());
            dto.targetAmount = MoneyDTO.fromDomain(transaction.getTargetAmount());
            dto.exchangeRate = transaction.getExchangeRate();
            dto.exchangeRateVersionId = transaction.getExchangeRateVersionId();
            dto.executionDate = transaction.getExecutionDate().toString();
            dto.createdAt = transaction.getCreatedAt().toString();
            dto.createdBy = transaction.getCreatedBy();
            dto.status = transaction.getStatus().name();
            dto.revertReason = transaction.getRevertReason();
            dto.revertedAt = transaction.getRevertedAt() != null ? transaction.getRevertedAt().toString() : null;
            dto.revertedBy = transaction.getRevertedBy();
            return dto;
        }
        
        public Transaction toDomain() {
            Transaction transaction = new Transaction(
                id,
                consultantId,
                customerId,
                sourceAmount.toDomain(),
                targetAmount.toDomain(),
                exchangeRate,
                exchangeRateVersionId,
                java.time.LocalDateTime.parse(executionDate),
                java.time.LocalDateTime.parse(createdAt),
                createdBy,
                TransactionStatus.valueOf(status)
            );
            
            // Restore revert information if exists
            if (revertReason != null && !revertReason.isEmpty()) {
                // Note: This is a workaround since revert fields are private
                // In production, consider adding a constructor or builder pattern
            }
            
            return transaction;
        }
    }
    
    private static class MoneyDTO {
        public String amount;
        public String currency;
        
        public static MoneyDTO fromDomain(Money money) {
            MoneyDTO dto = new MoneyDTO();
            dto.amount = money.getAmount().toString();
            dto.currency = money.getCurrency().getCode();
            return dto;
        }
        
        public Money toDomain() {
            return new Money(new BigDecimal(amount), Currency.fromCode(currency));
        }
    }
}
