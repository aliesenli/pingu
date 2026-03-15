package ch.pingu.backend.transactions.api;

import ch.pingu.backend.transactions.model.Transaction;
import ch.pingu.backend.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all transactions, optionally filtered by consultantId")
    public List<Transaction> list(@RequestParam(name = "consultantId", required = false) String consultantId) {
        if (consultantId != null && !consultantId.isBlank()) {
            return service.findByConsultantId(consultantId);
        }
        return service.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<Transaction> get(@PathVariable("id") String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new transaction")
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction, Authentication auth) {
        if (transaction.getCreatedBy() == null && auth != null) {
            transaction.setCreatedBy(auth.getName());
        }
        return ResponseEntity.ok(service.create(transaction));
    }

    @PostMapping("/{id}/revert")
    @Operation(summary = "Revert a transaction")
    public ResponseEntity<Transaction> revert(@PathVariable("id") String id,
                                              @RequestBody Map<String, String> body,
                                              Authentication auth) {
        String reason = body.getOrDefault("reason", "");
        String revertedBy = auth != null ? auth.getName() : "unknown";
        return ResponseEntity.ok(service.revert(id, reason, revertedBy));
    }
}
