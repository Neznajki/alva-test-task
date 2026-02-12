package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
    @JsonProperty("transaction_id") UUID transactionId,
    @JsonProperty("account_id") UUID accountId,
    @JsonProperty("amount") Integer amount,
    @JsonProperty("created_at") OffsetDateTime createdAt
) {}
