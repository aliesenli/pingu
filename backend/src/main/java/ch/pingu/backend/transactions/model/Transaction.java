package ch.pingu.backend.transactions.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    @Id
    private String id;
    private String consultantId;
    private String customerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "source_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "source_currency"))
    })
    private MoneyDTO sourceAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "target_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "target_currency"))
    })
    private MoneyDTO targetAmount;

    private double exchangeRate;
    private String exchangeRateVersionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[[:ss][.SSSSSS]]")
    private LocalDateTime executionDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[[:ss][.SSSSSS]]")
    private LocalDateTime createdAt;

    private String createdBy;
    private String status;
    private String revertReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[[:ss][.SSSSSS]]")
    private LocalDateTime revertedAt;

    private String revertedBy;

    @Embeddable
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MoneyDTO {
        private String amount;
        private String currency;

        public MoneyDTO() {}

        public MoneyDTO(String amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConsultantId() { return consultantId; }
    public void setConsultantId(String consultantId) { this.consultantId = consultantId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public MoneyDTO getSourceAmount() { return sourceAmount; }
    public void setSourceAmount(MoneyDTO sourceAmount) { this.sourceAmount = sourceAmount; }
    public MoneyDTO getTargetAmount() { return targetAmount; }
    public void setTargetAmount(MoneyDTO targetAmount) { this.targetAmount = targetAmount; }
    public double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(double exchangeRate) { this.exchangeRate = exchangeRate; }
    public String getExchangeRateVersionId() { return exchangeRateVersionId; }
    public void setExchangeRateVersionId(String exchangeRateVersionId) { this.exchangeRateVersionId = exchangeRateVersionId; }
    public LocalDateTime getExecutionDate() { return executionDate; }
    public void setExecutionDate(LocalDateTime executionDate) { this.executionDate = executionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRevertReason() { return revertReason; }
    public void setRevertReason(String revertReason) { this.revertReason = revertReason; }
    public LocalDateTime getRevertedAt() { return revertedAt; }
    public void setRevertedAt(LocalDateTime revertedAt) { this.revertedAt = revertedAt; }
    public String getRevertedBy() { return revertedBy; }
    public void setRevertedBy(String revertedBy) { this.revertedBy = revertedBy; }
}
