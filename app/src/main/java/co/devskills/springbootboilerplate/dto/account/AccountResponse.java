package co.devskills.springbootboilerplate.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class AccountResponse {

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("balance")
    private Integer balance;

    public AccountResponse() {
    }

    public AccountResponse(UUID accountId, Integer balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
