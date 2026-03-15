package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TransactionRepository {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TransactionRepository(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Optional<Transaction> findById(String id, String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/transactions/" + id, token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) return Optional.empty();
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return Optional.of(mapToDomain(objectMapper.readValue(response.body(), TransactionDTO.class)));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transaction " + id, e);
        }
    }

    public List<Transaction> findAll(String token) {
        return fetchList(baseUrl + "/api/transactions", token);
    }

    public List<Transaction> findByConsultantId(String consultantId, String token) {
        return fetchList(baseUrl + "/api/transactions?consultantId=" + consultantId, token);
    }

    public Transaction save(Transaction transaction, String token) {
        try {
            String body = objectMapper.writeValueAsString(mapToDTO(transaction));
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/transactions", token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) throw new RuntimeException("HTTP " + response.statusCode());
            return mapToDomain(objectMapper.readValue(response.body(), TransactionDTO.class));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    public Transaction revert(String id, String reason, String token) {
        try {
            String body = objectMapper.writeValueAsString(Map.of("reason", reason));
            HttpRequest request = HttpClientHelper.requestBuilder(baseUrl + "/api/transactions/" + id + "/revert", token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return mapToDomain(objectMapper.readValue(response.body(), TransactionDTO.class));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error reverting transaction " + id, e);
        }
    }

    private List<Transaction> fetchList(String url, String token) {
        try {
            HttpRequest request = HttpClientHelper.requestBuilder(url, token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            List<TransactionDTO> dtos = objectMapper.readValue(response.body(), new TypeReference<>() {});
            return dtos.stream().map(this::mapToDomain).toList();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching transactions", e);
        }
    }

    private Transaction mapToDomain(TransactionDTO dto) {
        Money source = new Money(new BigDecimal(dto.sourceAmount.amount.trim()), Currency.fromCode(dto.sourceAmount.currency));
        Money target = new Money(new BigDecimal(dto.targetAmount.amount.trim()), Currency.fromCode(dto.targetAmount.currency));
        return new Transaction(
                dto.id,
                dto.consultantId,
                dto.customerId,
                source,
                target,
                dto.exchangeRate,
                dto.exchangeRateVersionId,
                dto.executionDate,
                dto.createdAt,
                dto.createdBy,
                TransactionStatus.valueOf(dto.status)
        );
    }

    private TransactionDTO mapToDTO(Transaction t) {
        TransactionDTO dto = new TransactionDTO();
        dto.id = t.getId();
        dto.consultantId = t.getConsultantId();
        dto.customerId = t.getCustomerId();
        dto.sourceAmount = new TransactionDTO.MoneyDTO(
                t.getSourceAmount().getAmount().toPlainString(),
                t.getSourceAmount().getCurrency().getCode());
        dto.targetAmount = new TransactionDTO.MoneyDTO(
                t.getTargetAmount().getAmount().toPlainString(),
                t.getTargetAmount().getCurrency().getCode());
        dto.exchangeRate = t.getExchangeRate();
        dto.exchangeRateVersionId = t.getExchangeRateVersionId();
        dto.executionDate = t.getExecutionDate();
        dto.createdAt = t.getCreatedAt();
        dto.createdBy = t.getCreatedBy();
        dto.status = t.getStatus().name();
        dto.revertReason = t.getRevertReason();
        dto.revertedAt = t.getRevertedAt();
        dto.revertedBy = t.getRevertedBy();
        return dto;
    }

    // DTO matching backend JSON shape — needs no-arg constructor for Jackson
    static class TransactionDTO {
        public TransactionDTO() {}
        public String id;
        public String consultantId;
        public String customerId;
        public MoneyDTO sourceAmount;
        public MoneyDTO targetAmount;
        public double exchangeRate;
        public String exchangeRateVersionId;
        public LocalDateTime executionDate;
        public LocalDateTime createdAt;
        public String createdBy;
        public String status;
        public String revertReason;
        public LocalDateTime revertedAt;
        public String revertedBy;

        static class MoneyDTO {
            public MoneyDTO() {}
            public MoneyDTO(String amount, String currency) {
                this.amount = amount;
                this.currency = currency;
            }
            public String amount;
            public String currency;
        }
    }
}
