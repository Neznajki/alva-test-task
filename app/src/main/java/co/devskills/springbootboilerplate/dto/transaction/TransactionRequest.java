package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class TransactionRequest {

    @JsonProperty("transaction_id")
    private UUID transactionId;

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("amount")
    private Integer amount;

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
}
