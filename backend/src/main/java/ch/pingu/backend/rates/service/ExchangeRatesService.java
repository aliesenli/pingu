package ch.pingu.backend.rates.service;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRatesService {

    private static final String DATA_PATH = "data/exchange-rates.json";

    private final ObjectMapper objectMapper;
    private final Map<String, ExchangeRateVersion> byId = new ConcurrentHashMap<>();

    public ExchangeRatesService() throws IOException {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        loadInitial();
    }

    private void loadInitial() throws IOException {
        ClassPathResource res = new ClassPathResource(DATA_PATH);
        try (InputStream is = res.getInputStream()) {
            List<ExchangeRateVersion> list = objectMapper.readValue(is, new TypeReference<>(){});
            list.forEach(v -> byId.put(v.getId(), v));
        }
    }

    public List<ExchangeRateVersion> listAll() {
        return byId.values().stream()
                .sorted(Comparator.comparing(ExchangeRateVersion::isActive).reversed()
                        .thenComparing(ExchangeRateVersion::getUploadedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public Optional<ExchangeRateVersion> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public Optional<ExchangeRateVersion> findActive() {
        return byId.values().stream().filter(ExchangeRateVersion::isActive).findFirst();
    }

    public ExchangeRateVersion create(ExchangeRateVersion version) {
        Objects.requireNonNull(version.getId(), "id is required");
        byId.put(version.getId(), version);
        if (version.isActive()) {
            byId.values().forEach(v -> { if (!Objects.equals(v.getId(), version.getId())) v.setActive(false); });
        }
        return version;
    }

    public BigDecimal convert(String versionId, String from, String to, BigDecimal amount) {
        ExchangeRateVersion v = findById(versionId).orElseThrow(() -> new NoSuchElementException("Version not found"));
        Map<String, BigDecimal> rates = v.getRates();
        if (rates == null) throw new IllegalStateException("No rates in version");
        BigDecimal fromRate = rates.get(from);
        BigDecimal toRate = rates.get(to);
        if (fromRate == null || toRate == null) {
            throw new NoSuchElementException("Currency not found");
        }
        MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
        BigDecimal baseAmount = amount.divide(fromRate, mc);
        return baseAmount.multiply(toRate, mc);
    }
}
