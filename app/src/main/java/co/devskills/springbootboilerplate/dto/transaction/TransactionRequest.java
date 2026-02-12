package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record TransactionRequest(
    @JsonProperty("transaction_id") UUID transactionId,
    @JsonProperty("account_id") UUID accountId,
    @JsonProperty("amount") Integer amount
) {
    public TransactionRequest withGeneratedIdIfMissing() {
        if (this.transactionId != null) {
            return this;
        }
        return new TransactionRequest(UUID.randomUUID(), this.accountId, this.amount);
    }
}
