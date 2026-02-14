package ch.pingu.domain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a versioned set of exchange rates with a base currency
 */
public class ExchangeRateVersion {
    
    private final String id;
    private final String versionName;
    private final Currency baseCurrency;
    private final Map<Currency, Double> rates;
    private final LocalDateTime uploadedAt;
    private final String uploadedBy;
    private boolean active;
    
    public ExchangeRateVersion(String id, String versionName, Currency baseCurrency, 
                               Map<Currency, Double> rates, LocalDateTime uploadedAt, 
                               String uploadedBy, boolean active) {
        this.id = id;
        this.versionName = versionName;
        this.baseCurrency = baseCurrency;
        this.rates = new HashMap<>(rates);
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
        this.active = active;
    }
    
    public static ExchangeRateVersion create(String versionName, Currency baseCurrency, 
                                            Map<Currency, Double> rates, String uploadedBy) {
        return new ExchangeRateVersion(
            UUID.randomUUID().toString(),
            versionName,
            baseCurrency,
            rates,
            LocalDateTime.now(),
            uploadedBy,
            false
        );
    }
    
    public String getId() {
        return id;
    }
    
    public String getVersionName() {
        return versionName;
    }
    
    public Currency getBaseCurrency() {
        return baseCurrency;
    }
    
    public Map<Currency, Double> getRates() {
        return new HashMap<>(rates);
    }
    
    public Double getRate(Currency currency) {
        return rates.get(currency);
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateVersion that = (ExchangeRateVersion) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
