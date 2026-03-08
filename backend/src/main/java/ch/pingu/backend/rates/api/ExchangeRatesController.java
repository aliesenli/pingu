package ch.pingu.backend.rates.api;

import ch.pingu.backend.rates.model.ExchangeRateVersion;
import ch.pingu.backend.rates.service.ExchangeRatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
@Tag(name = "Exchange Rates")
public class ExchangeRatesController {

    private final ExchangeRatesService service;

    public ExchangeRatesController(ExchangeRatesService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all exchange rate versions")
    public List<ExchangeRateVersion> list() {
        return service.listAll();
    }

    @GetMapping("/active")
    @Operation(summary = "Get currently active exchange rate version")
    public ResponseEntity<ExchangeRateVersion> active() {
        return service.findActive().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange rate version by id")
    public ResponseEntity<ExchangeRateVersion> get(@PathVariable String id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/convert")
    @Operation(summary = "Convert an amount using a version's rates")
    public ResponseEntity<BigDecimal> convert(@PathVariable String id,
                                              @RequestParam String from,
                                              @RequestParam String to,
                                              @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(service.convert(id, from, to, amount));
    }

    @PostMapping
    @Operation(summary = "Create or update a rate version (in-memory only)")
    public ResponseEntity<ExchangeRateVersion> create(@Valid @RequestBody ExchangeRateVersion version) {
        return ResponseEntity.ok(service.create(version));
    }
}
