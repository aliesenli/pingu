package ch.pingu.backend.seed;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import ch.pingu.backend.rates.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DataSeeder implements ApplicationRunner {

    private final ExchangeRatesService service;

    @Value("${seed.enabled:true}")
    private boolean enabled;

    @Value("${seed.count:3}")
    private int count;

    @Value("${seed.baseDate:}")
    private String baseDateProp;

    @Value("${seed.uploadedBy:admin}")
    private String uploadedBy;

    @Value("${seed.activeLatest:false}")
    private boolean activeLatest;

    public DataSeeder(ExchangeRatesService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled || count <= 0) return;

        Map<String, BigDecimal> baseRates = new LinkedHashMap<>();
        Optional<ExchangeRateVersion> baseVersion = service.findActive().or(() -> service.listAll().stream().findFirst());
        if (baseVersion.isPresent() && baseVersion.get().getRates() != null && !baseVersion.get().getRates().isEmpty()) {
            baseRates.putAll(baseVersion.get().getRates());
        } else {
            baseRates.put("CHF", bd(1));
            baseRates.put("USD", bd(1.1));
            baseRates.put("EUR", bd(1.0));
            baseRates.put("GBP", bd(0.85));
            baseRates.put("JPY", bd(160));
        }

        LocalDate baseDate = parseBaseDate(baseDateProp);
        DateTimeFormatter nameFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MathContext mc = new MathContext(18, RoundingMode.HALF_UP);

        for (int i = 1; i <= count; i++) {
            LocalDate day = baseDate.minusDays(i);
            String id = "seed-version-" + day.format(DateTimeFormatter.BASIC_ISO_DATE);
            if (service.findById(id).isPresent()) continue; // avoid duplicates across restarts

            Map<String, BigDecimal> varied = new HashMap<>();
            int idx = i;
            baseRates.forEach((code, rate) -> {
                int h = Math.abs(code.hashCode());
                BigDecimal delta = new BigDecimal(((h % 7) - 3) * idx).divide(new BigDecimal("1000"), mc);
                BigDecimal onePlusDelta = BigDecimal.ONE.add(delta, mc);
                BigDecimal newRate = rate.multiply(onePlusDelta, mc);
                if (isBaseCurrency(code, baseVersion.map(ExchangeRateVersion::getBaseCurrency).orElse("CHF"))) {
                    newRate = BigDecimal.ONE;
                }
                varied.put(code, newRate);
            });

            ExchangeRateVersion v = new ExchangeRateVersion();
            v.setId(id);
            v.setVersionName(day.format(nameFmt) + " Daily Rates");
            v.setBaseCurrency(baseVersion.map(ExchangeRateVersion::getBaseCurrency).orElse("CHF"));
            v.setRates(varied);
            v.setUploadedAt(LocalDateTime.of(day, java.time.LocalTime.MIDNIGHT));
            v.setUploadedBy(uploadedBy);
            v.setActive(false);

            service.create(v);
        }

        if (activeLatest && count > 0) {
            LocalDate latest = baseDate.minusDays(1);
            String latestId = "seed-version-" + latest.format(DateTimeFormatter.BASIC_ISO_DATE);
            service.findById(latestId).ifPresent(v -> {
                v.setActive(true);
                service.create(v);
            });
        }
    }

    private static boolean isBaseCurrency(String code, String base) {
        return code != null && code.equalsIgnoreCase(base);
    }

    private static LocalDate parseBaseDate(String prop) {
        if (prop == null || prop.isBlank()) return LocalDate.now();
        try {
            return LocalDate.parse(prop);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private static BigDecimal bd(double v) {
        return new BigDecimal(Double.toString(v));
    }
}
