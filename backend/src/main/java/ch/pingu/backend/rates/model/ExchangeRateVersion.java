package ch.pingu.backend.rates.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "exchange_rate_versions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateVersion {

    @Id
    private String id;
    private String versionName;
    private String baseCurrency;

    @ElementCollection
    @CollectionTable(name = "exchange_rates", joinColumns = @JoinColumn(name = "version_id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "rate")
    private Map<String, BigDecimal> rates;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[[:ss][.SSSSSS]]")
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private boolean active;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVersionName() { return versionName; }
    public void setVersionName(String versionName) { this.versionName = versionName; }
    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public Map<String, BigDecimal> getRates() { return rates; }
    public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
