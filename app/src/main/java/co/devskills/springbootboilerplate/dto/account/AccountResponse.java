package co.devskills.springbootboilerplate.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record AccountResponse(
    @JsonProperty("account_id") UUID accountId,
    @JsonProperty("balance") Integer balance
) {}
