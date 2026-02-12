package co.devskills.springbootboilerplate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionsIntegrationTest {
    
    private record TestTransactionRequest(
            @JsonProperty("account_id") Object accountId,
            @JsonProperty("amount") Object amount
    ) {}

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void providesAFunctionalHealthcheck() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    void canCreateAndReadTransactionsAndAccountsWithPositiveAmounts() throws Exception {
        UUID accountId = UUID.randomUUID();
        int amount = (int) (Math.random() * 100) + 1;

        TestTransactionRequest request = new TestTransactionRequest(accountId.toString(), amount);

        String responseJson = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").exists())
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.amount").value(amount))
                .andReturn().getResponse().getContentAsString();

        String transactionId = objectMapper.readTree(responseJson).get("transaction_id").asText();

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction_id").value(transactionId))
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.amount").value(amount));

        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.balance").value(amount));
    }

    @Test
    void canCreateAndReadTransactionsAndAccountsWithNegativeAmounts() throws Exception {
        UUID accountId = UUID.randomUUID();
        int initialAmount = (int) (Math.random() * 100) + 10;
        int negativeAmount = -((int) (Math.random() * 5) + 1);

        // Create first transaction
        TestTransactionRequest request1 = new TestTransactionRequest(accountId.toString(), initialAmount);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Check account balance
        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(initialAmount));

        // Create second transaction (negative)
        TestTransactionRequest request2 = new TestTransactionRequest(accountId.toString(), negativeAmount);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // Check final balance
        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(initialAmount + negativeAmount));
    }

    @Test
    void canHandleRequestsForNonExistentAccountsAndTransactions() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("invalidPostTransactionRequests")
    void canHandleInvalidPostRequests(String contentType, String body, int expectedStatus) throws Exception {
        var requestBuilder = post("/transactions");
        requestBuilder.contentType(MediaType.parseMediaType(contentType));
        requestBuilder.content(body);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));
    }

    @ParameterizedTest
    @MethodSource("invalidPutTransactionRequests")
    void canHandleInvalidPutRequests(String contentType, String body, int expectedStatus) throws Exception {
        var requestBuilder = put("/transactions");
        requestBuilder.contentType(MediaType.parseMediaType(contentType));
        requestBuilder.content(body);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));
    }

    private static Stream<Arguments> invalidPostTransactionRequests() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String accountId = UUID.randomUUID().toString();

        var missingAccountId = new TestTransactionRequest(null, 7);
        var missingAmount = new TestTransactionRequest(accountId, null);
        var malformedUuid = new TestTransactionRequest("not-a-uuid", 7);
        var stringAmount = new TestTransactionRequest(accountId, "high");

        return Stream.of(
                // 1. Wrong Content-Type
                Arguments.of(MediaType.APPLICATION_XML_VALUE, "<request></request>", 415),
                // 2. Missing account_id
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(missingAccountId), 400),
                // 3. Missing amount
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(missingAmount), 400),
                // 4. Malformed UUID
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(malformedUuid), 400),
                // 5. String instead of integer for amount
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(stringAmount), 400)
        );
    }

    private static Stream<Arguments> invalidPutTransactionRequests() {
        return Stream.of(
                // 1. Wrong method (PUT on /transactions which only supports POST and GET)
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, "{}", 405)
        );
    }
}
