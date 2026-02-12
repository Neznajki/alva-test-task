package co.devskills.springbootboilerplate.controller;

import co.devskills.springbootboilerplate.dto.account.AccountResponse;
import co.devskills.springbootboilerplate.service.TransactionService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {

    private final TransactionService transactionService;

    public AccountsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/accounts/{account_id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("account_id") String accountIdStr) {
        UUID accountId = UUID.fromString(accountIdStr);

        return transactionService.getAccountById(accountId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
