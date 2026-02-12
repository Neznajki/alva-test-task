package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TransactionRequest(
    @JsonProperty("transaction_id") UUID transactionId,
    @JsonProperty("account_id") @NotNull UUID accountId,
    @JsonProperty("amount") @NotNull Integer amount
) {

}
