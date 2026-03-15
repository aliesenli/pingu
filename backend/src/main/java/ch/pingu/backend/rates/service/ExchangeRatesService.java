package ch.pingu.backend.rates.service;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import ch.pingu.backend.rates.repository.ExchangeRateVersionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ExchangeRatesService {

    private final ExchangeRateVersionRepository repository;

    public ExchangeRatesService(ExchangeRateVersionRepository repository) {
        this.repository = repository;
    }

    public List<ExchangeRateVersion> listAll() {
        return repository.findAll(Sort.by(
                Sort.Order.desc("active"),
                Sort.Order.desc("uploadedAt")
        ));
    }

    public Optional<ExchangeRateVersion> findById(String id) {
        return repository.findById(id);
    }

    public Optional<ExchangeRateVersion> findActive() {
        return repository.findByActiveTrue();
    }

    @Transactional
    public ExchangeRateVersion create(ExchangeRateVersion version) {
        if (version.isActive()) {
            repository.deactivateAll();
        }
        return repository.save(version);
    }

    @Transactional
    public ExchangeRateVersion activate(String id) {
        ExchangeRateVersion version = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Version not found: " + id));
        repository.deactivateAll();
        version.setActive(true);
        return repository.save(version);
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
