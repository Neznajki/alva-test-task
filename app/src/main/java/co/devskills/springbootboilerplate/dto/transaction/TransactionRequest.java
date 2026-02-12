package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionRequest {

    @JsonProperty("transaction_id")
    private UUID transactionId;

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("amount")
    private Integer amount;
}
