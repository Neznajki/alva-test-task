package co.devskills.springbootboilerplate.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    @JsonProperty("transaction_id")
    private UUID transactionId;

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
