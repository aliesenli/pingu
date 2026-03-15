package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.Currency;
import ch.pingu.domain.model.ExchangeRateVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExchangeRateRepository {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExchangeRateRepository(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Optional<ExchangeRateVersion> findById(String id, String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/rates/" + id, token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) return Optional.empty();
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return Optional.of(mapToDomain(objectMapper.readValue(response.body(), ExchangeRateVersionDTO.class)));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching rate version " + id, e);
        }
    }

    public Optional<ExchangeRateVersion> findActiveVersion(String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/rates/active", token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) return Optional.empty();
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return Optional.of(mapToDomain(objectMapper.readValue(response.body(), ExchangeRateVersionDTO.class)));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active rate version", e);
        }
    }

    public List<ExchangeRateVersion> findAll(String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/rates", token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            List<ExchangeRateVersionDTO> dtos = objectMapper.readValue(response.body(), new TypeReference<>() {});
            return dtos.stream().map(this::mapToDomain).toList();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching rate versions", e);
        }
    }

    public ExchangeRateVersion save(ExchangeRateVersion version, String token) {
        try {
            String body = objectMapper.writeValueAsString(mapToDTO(version));
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/rates", token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) throw new RuntimeException("HTTP " + response.statusCode());
            return mapToDomain(objectMapper.readValue(response.body(), ExchangeRateVersionDTO.class));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving rate version", e);
        }
    }

    public ExchangeRateVersion setActiveVersion(String versionId, String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/rates/" + versionId + "/activate", token)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return mapToDomain(objectMapper.readValue(response.body(), ExchangeRateVersionDTO.class));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error activating rate version " + versionId, e);
        }
    }

    private ExchangeRateVersion mapToDomain(ExchangeRateVersionDTO dto) {
        Map<Currency, Double> currencyRates = new HashMap<>();
        if (dto.rates != null) {
            dto.rates.forEach((code, rate) ->
                    currencyRates.put(Currency.fromCode(code), rate.doubleValue()));
        }
        return new ExchangeRateVersion(
                dto.id,
                dto.versionName,
                Currency.fromCode(dto.baseCurrency),
                currencyRates,
                dto.uploadedAt,
                dto.uploadedBy,
                dto.active
        );
    }

    private ExchangeRateVersionDTO mapToDTO(ExchangeRateVersion version) {
        ExchangeRateVersionDTO dto = new ExchangeRateVersionDTO();
        dto.id = version.getId();
        dto.versionName = version.getVersionName();
        dto.baseCurrency = version.getBaseCurrency().getCode();
        dto.rates = new HashMap<>();
        version.getRates().forEach((currency, rate) ->
                dto.rates.put(currency.getCode(), BigDecimal.valueOf(rate)));
        dto.uploadedAt = version.getUploadedAt();
        dto.uploadedBy = version.getUploadedBy();
        dto.active = version.isActive();
        return dto;
    }

    // DTO needs no-arg constructor for Jackson deserialization
    static class ExchangeRateVersionDTO {
        public ExchangeRateVersionDTO() {}
        public String id;
        public String versionName;
        public String baseCurrency;
        public Map<String, BigDecimal> rates;
        public LocalDateTime uploadedAt;
        public String uploadedBy;
        public boolean active;
    }
}
