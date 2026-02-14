package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.Currency;
import ch.pingu.domain.model.ExchangeRateVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * JSON-based repository for ExchangeRateVersion entities
 */
public class ExchangeRateRepository {
    
    private final ObjectMapper objectMapper;
    private final String filePath;
    private List<ExchangeRateVersion> versions;
    
    public ExchangeRateRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.versions = new ArrayList<>();
        loadVersions();
    }
    
    private void loadVersions() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                List<ExchangeRateVersionDTO> dtos = objectMapper.readValue(file, new TypeReference<List<ExchangeRateVersionDTO>>() {});
                versions = dtos.stream()
                    .map(ExchangeRateVersionDTO::toDomain)
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                System.err.println("Error loading exchange rates: " + e.getMessage());
                versions = new ArrayList<>();
            }
        }
    }
    
    private void saveVersions() {
        try {
            List<ExchangeRateVersionDTO> dtos = versions.stream()
                .map(ExchangeRateVersionDTO::fromDomain)
                .toList();
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(filePath), dtos);
        } catch (IOException e) {
            throw new RuntimeException("Error saving exchange rates", e);
        }
    }
    
    public Optional<ExchangeRateVersion> findById(String id) {
        return versions.stream()
            .filter(v -> v.getId().equals(id))
            .findFirst();
    }
    
    public Optional<ExchangeRateVersion> findActiveVersion() {
        return versions.stream()
            .filter(ExchangeRateVersion::isActive)
            .findFirst();
    }
    
    public List<ExchangeRateVersion> findAll() {
        return new ArrayList<>(versions);
    }
    
    public void save(ExchangeRateVersion version) {
        versions.removeIf(v -> v.getId().equals(version.getId()));
        versions.add(version);
        saveVersions();
    }
    
    public void setActiveVersion(String versionId) {
        versions.forEach(v -> v.setActive(false));
        versions.stream()
            .filter(v -> v.getId().equals(versionId))
            .findFirst()
            .ifPresent(v -> v.setActive(true));
        saveVersions();
    }
    
    // DTO for JSON serialization
    private static class ExchangeRateVersionDTO {
        public String id;
        public String versionName;
        public String baseCurrency;
        public Map<String, Double> rates;
        public String uploadedAt;
        public String uploadedBy;
        public boolean active;
        
        public static ExchangeRateVersionDTO fromDomain(ExchangeRateVersion version) {
            ExchangeRateVersionDTO dto = new ExchangeRateVersionDTO();
            dto.id = version.getId();
            dto.versionName = version.getVersionName();
            dto.baseCurrency = version.getBaseCurrency().getCode();
            dto.rates = new HashMap<>();
            version.getRates().forEach((currency, rate) -> 
                dto.rates.put(currency.getCode(), rate)
            );
            dto.uploadedAt = version.getUploadedAt().toString();
            dto.uploadedBy = version.getUploadedBy();
            dto.active = version.isActive();
            return dto;
        }
        
        public ExchangeRateVersion toDomain() {
            Map<Currency, Double> currencyRates = new HashMap<>();
            rates.forEach((code, rate) -> 
                currencyRates.put(Currency.fromCode(code), rate)
            );
            
            return new ExchangeRateVersion(
                id,
                versionName,
                Currency.fromCode(baseCurrency),
                currencyRates,
                java.time.LocalDateTime.parse(uploadedAt),
                uploadedBy,
                active
            );
        }
    }
}
