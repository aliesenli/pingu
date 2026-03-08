package ch.pingu.backend.rates.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateVersion {
    private String id;
    private String versionName;
    private String baseCurrency;
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
