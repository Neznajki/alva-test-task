package co.devskills.springbootboilerplate.controller;

import co.devskills.springbootboilerplate.dto.transaction.TransactionRequest;
import co.devskills.springbootboilerplate.dto.transaction.TransactionResponse;
import co.devskills.springbootboilerplate.service.TransactionService;
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
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request) {
        if (request.getTransactionId() == null || request.getAccountId() == null || request.getAmount() == null) {
            return ResponseEntity.badRequest().build();
        }
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping(value = "/transactions/{transaction_id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("transaction_id") String transactionIdStr) {
        UUID transactionId;
        try {
            transactionId = UUID.fromString(transactionIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
