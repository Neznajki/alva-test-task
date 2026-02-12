package co.devskills.springbootboilerplate.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    @JsonProperty("account_id")
    private UUID accountId;

    @JsonProperty("balance")
    private Integer balance;
}
