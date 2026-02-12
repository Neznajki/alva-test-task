package co.devskills.springbootboilerplate.controller;

import co.devskills.springbootboilerplate.dto.transaction.TransactionRequest;
import co.devskills.springbootboilerplate.dto.transaction.TransactionResponse;
import co.devskills.springbootboilerplate.exception.ItemNotFoundException;
import co.devskills.springbootboilerplate.helper.TransactionRequestHelper;
import co.devskills.springbootboilerplate.service.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {

    private final TransactionService transactionService;

    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transactions")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.create(TransactionRequestHelper.withGeneratedIdIfMissing(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/transactions")
    public ResponseEntity<List<TransactionResponse>> findAll() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping(value = "/transactions/{transaction_id}")
    public ResponseEntity<?> findById(@PathVariable("transaction_id") String transactionIdStr) {
        UUID transactionId = UUID.fromString(transactionIdStr);

        return transactionService.findById(transactionId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ItemNotFoundException("Transaction not found"));
    }
}
