package ch.pingu.backend.seed;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import ch.pingu.backend.rates.service.ExchangeRatesService;
import ch.pingu.backend.transactions.model.Transaction;
import ch.pingu.backend.transactions.service.TransactionService;
import ch.pingu.backend.users.model.UserInfo;
import ch.pingu.backend.users.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DataSeeder implements ApplicationRunner {

    private final ExchangeRatesService ratesService;
    private final TransactionService transactionService;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.enabled:true}")
    private boolean enabled;
    @Value("${seed.force_users:true}")
    private boolean seedUsers;

    @Value("${seed.count:3}")
    private int count;

    @Value("${seed.baseDate:}")
    private String baseDateProp;

    @Value("${seed.uploadedBy:admin}")
    private String uploadedBy;

    @Value("${seed.activeLatest:true}")
    private boolean activeLatest;

    public DataSeeder(ExchangeRatesService ratesService, TransactionService transactionService,
                      UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.ratesService = ratesService;
        this.transactionService = transactionService;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedUsers && !enabled || count <= 0) {
            return;
        }

        if (seedUsers) {
            seedUsers();
        }

        if (!enabled) {
            return;
        }

        seedRates();
        seedTransactions();
    }

    private void seedUsers() {
        if (userInfoRepository.count() > 0) return;

        String encodedPassword = passwordEncoder.encode("password");
        userInfoRepository.save(new UserInfo("user-001", "admin", encodedPassword, "ADMIN", LocalDateTime.parse("2026-01-01T10:00")));
        userInfoRepository.save(new UserInfo("user-002", "consultant1", encodedPassword, "CONSULTANT", LocalDateTime.parse("2026-01-15T14:30")));
        userInfoRepository.save(new UserInfo("user-003", "consultant2", encodedPassword, "CONSULTANT", LocalDateTime.parse("2026-01-20T09:15")));
    }

    private void seedRates() {
        Map<String, BigDecimal> baseRates = new LinkedHashMap<>();
        Optional<ExchangeRateVersion> baseVersion = ratesService.findActive()
                .or(() -> ratesService.listAll().stream().findFirst());

        if (baseVersion.isPresent() && baseVersion.get().getRates() != null && !baseVersion.get().getRates().isEmpty()) {
            baseRates.putAll(baseVersion.get().getRates());
        } else {
            baseRates.put("CHF", bd(1));
            baseRates.put("EUR", bd(1.05));
            baseRates.put("USD", bd(1.15));
            baseRates.put("GBP", bd(0.85));
            baseRates.put("JPY", bd(165.5));
            baseRates.put("CAD", bd(1.55));
            baseRates.put("AUD", bd(1.70));
            baseRates.put("CNY", bd(8.30));
            baseRates.put("INR", bd(96.0));
            baseRates.put("SEK", bd(11.80));
        }

        LocalDate baseDate = parseBaseDate(baseDateProp);
        DateTimeFormatter nameFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MathContext mc = new MathContext(18, RoundingMode.HALF_UP);

        for (int i = 1; i <= count; i++) {
            LocalDate day = baseDate.minusDays(i);
            String id = "seed-version-" + day.format(DateTimeFormatter.BASIC_ISO_DATE);
            if (ratesService.findById(id).isPresent()) continue;

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
            v.setUploadedAt(LocalDateTime.of(day, LocalTime.MIDNIGHT));
            v.setUploadedBy(uploadedBy);
            v.setActive(false);

            ratesService.create(v);
        }

        if (activeLatest && count > 0) {
            LocalDate latest = baseDate.minusDays(1);
            String latestId = "seed-version-" + latest.format(DateTimeFormatter.BASIC_ISO_DATE);
            ratesService.findById(latestId).ifPresent(v -> {
                v.setActive(true);
                ratesService.create(v);
            });
        }
    }

    private void seedTransactions() {
        if (transactionService.findById("txn-001").isPresent()) return;

        createTxn("txn-001", "user-002", "customer-001",
                "1000.00", "CHF", "1050.00", "EUR", 1.05,
                "2026-02-10T10:30", "2026-02-10T10:25", "consultant1", "COMPLETED");

        createTxn("txn-002", "user-002", "customer-002",
                "5000.00", "USD", "4347.83", "CHF", 0.86956522,
                "2026-02-11T14:15", "2026-02-11T14:10", "consultant1", "EXECUTED");

        createTxn("txn-003", "user-003", "customer-003",
                "2000.00", "EUR", "1904.76", "CHF", 0.95238095,
                "2026-02-12T09:45", "2026-02-12T09:40", "consultant2", "COMPLETED");

        createTxn("txn-004", "user-002", "customer-001",
                "10000.00", "JPY", "60.42", "CHF", 0.00604217,
                "2026-02-13T11:20", "2026-02-13T11:15", "consultant1", "NOT_STARTED");

        createTxn("txn-005", "user-003", "customer-004",
                "800.00", "GBP", "941.18", "CHF", 1.17647059,
                "2026-02-09T16:00", "2026-02-09T15:55", "consultant2", "REVERTED");

        createTxn("txn-006", "user-002", "customer-002",
                "3000.00", "CHF", "3450.00", "USD", 1.15,
                "2026-02-08T13:30", "2026-02-08T13:25", "consultant1", "REVERTED");
    }

    private void createTxn(String id, String consultantId, String customerId,
                           String srcAmt, String srcCur, String tgtAmt, String tgtCur,
                           double rate, String execDate, String createdAt,
                           String createdBy, String status) {
        String rateVersionId = ratesService.findActive()
                .map(ExchangeRateVersion::getId)
                .orElse("seed-version-unknown");

        Transaction txn = new Transaction();
        txn.setId(id);
        txn.setConsultantId(consultantId);
        txn.setCustomerId(customerId);
        txn.setSourceAmount(new Transaction.MoneyDTO(srcAmt, srcCur));
        txn.setTargetAmount(new Transaction.MoneyDTO(tgtAmt, tgtCur));
        txn.setExchangeRate(rate);
        txn.setExchangeRateVersionId(rateVersionId);
        txn.setExecutionDate(LocalDateTime.parse(execDate));
        txn.setCreatedAt(LocalDateTime.parse(createdAt));
        txn.setCreatedBy(createdBy);
        txn.setStatus(status);
        transactionService.create(txn);
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
