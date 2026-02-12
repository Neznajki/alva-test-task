package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransactionResponse {

    @JsonProperty("transaction_id")
    private UUID transactionId;

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    public TransactionResponse() {
    }

    public TransactionResponse(UUID transactionId, UUID accountId, Integer amount, OffsetDateTime createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
