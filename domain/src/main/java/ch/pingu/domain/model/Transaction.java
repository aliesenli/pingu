package ch.pingu.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a currency exchange transaction
 */
public class Transaction {
    
    private final String id;
    private final String consultantId;
    private final String customerId;
    private final Money sourceAmount;
    private final Money targetAmount;
    private final double exchangeRate;
    private final String exchangeRateVersionId;
    private final LocalDateTime executionDate;
    private final LocalDateTime createdAt;
    private final String createdBy;
    private TransactionStatus status;
    private String revertReason;
    private LocalDateTime revertedAt;
    private String revertedBy;
    
    public Transaction(String id, String consultantId, String customerId,
                      Money sourceAmount, Money targetAmount, double exchangeRate,
                      String exchangeRateVersionId, LocalDateTime executionDate,
                      LocalDateTime createdAt, String createdBy, TransactionStatus status) {
        this.id = id;
        this.consultantId = consultantId;
        this.customerId = customerId;
        this.sourceAmount = sourceAmount;
        this.targetAmount = targetAmount;
        this.exchangeRate = exchangeRate;
        this.exchangeRateVersionId = exchangeRateVersionId;
        this.executionDate = executionDate;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.status = status;
    }
    
    public static Transaction create(String consultantId, String customerId,
                                    Money sourceAmount, Money targetAmount,
                                    double exchangeRate, String exchangeRateVersionId,
                                    LocalDateTime executionDate, String createdBy) {
        return new Transaction(
            UUID.randomUUID().toString(),
            consultantId,
            customerId,
            sourceAmount,
            targetAmount,
            exchangeRate,
            exchangeRateVersionId,
            executionDate,
            LocalDateTime.now(),
            createdBy,
            TransactionStatus.NOT_STARTED
        );
    }
    
    public void updateStatus(TransactionStatus newStatus) {
        if (this.status == TransactionStatus.REVERTED) {
            throw new IllegalStateException("Cannot change status of a reverted transaction");
        }
        this.status = newStatus;
    }
    
    public void revert(String reason, String revertedBy) {
        if (this.status == TransactionStatus.REVERTED) {
            throw new IllegalStateException("Transaction already reverted");
        }
        this.status = TransactionStatus.REVERTED;
        this.revertReason = reason;
        this.revertedAt = LocalDateTime.now();
        this.revertedBy = revertedBy;
    }
    
    public String getId() {
        return id;
    }
    
    public String getConsultantId() {
        return consultantId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public Money getSourceAmount() {
        return sourceAmount;
    }
    
    public Money getTargetAmount() {
        return targetAmount;
    }
    
    public double getExchangeRate() {
        return exchangeRate;
    }
    
    public String getExchangeRateVersionId() {
        return exchangeRateVersionId;
    }
    
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public String getRevertReason() {
        return revertReason;
    }
    
    public LocalDateTime getRevertedAt() {
        return revertedAt;
    }
    
    public String getRevertedBy() {
        return revertedBy;
    }
    
    public boolean isReverted() {
        return status == TransactionStatus.REVERTED;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
