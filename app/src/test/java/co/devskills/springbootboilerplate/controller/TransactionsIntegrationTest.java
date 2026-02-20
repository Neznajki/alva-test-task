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
            @JsonProperty("transaction_id") Object transactionId,
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
                .andExpect(content().string("{\"description\":\"The service is up and running\"}"));
    }

    @Test
    void canCreateTransactionWithExplicitId() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        int amount = 50;

        TestTransactionRequest request = new TestTransactionRequest(transactionId.toString(), accountId.toString(), amount);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").value(transactionId.toString()))
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.amount").value(amount));

        // Verify it can be retrieved by that ID
        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction_id").value(transactionId.toString()))
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    void canCreateAndReadTransactionsAndAccountsWithPositiveAmounts() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        int amount = (int) (Math.random() * 100) + 1;

        TestTransactionRequest request = new TestTransactionRequest(transactionId.toString(), accountId.toString(), amount);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id").value(transactionId.toString()))
                .andExpect(jsonPath("$.account_id").value(accountId.toString()))
                .andExpect(jsonPath("$.amount").value(amount));

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction_id").value(transactionId.toString()))
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
        UUID transactionId1 = UUID.randomUUID();
        UUID transactionId2 = UUID.randomUUID();
        int initialAmount = (int) (Math.random() * 100) + 10;
        int negativeAmount = -((int) (Math.random() * 5) + 1);

        // Create first transaction
        TestTransactionRequest request1 = new TestTransactionRequest(transactionId1.toString(), accountId.toString(), initialAmount);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Check account balance
        mockMvc.perform(get("/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(initialAmount));

        // Create second transaction (negative)
        TestTransactionRequest request2 = new TestTransactionRequest(transactionId2.toString(), accountId.toString(), negativeAmount);

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
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"description\":\"Account not found.\"}"));

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"description\":\"Transaction not found\"}"));
    }

    @Test
    void canFindAllTransactions() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID transactionId1 = UUID.randomUUID();
        UUID transactionId2 = UUID.randomUUID();
        int amount1 = 10;
        int amount2 = 20;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TestTransactionRequest(transactionId1.toString(), accountId.toString(), amount1))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TestTransactionRequest(transactionId2.toString(), accountId.toString(), amount2))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.amount == %d)]", amount1).exists())
                .andExpect(jsonPath("$.[?(@.amount == %d)]", amount2).exists());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestWithDescription")
    void canHandleIllegalArgumentExceptionForInvalidTransactionId(String requestPath, String expectedDescription) throws Exception {
        mockMvc.perform(get("/%s/invalid-uuid".formatted(requestPath)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedDescription));
    }

    private static Stream<Arguments> invalidRequestWithDescription() {
        return Stream.of(
                Arguments.of("transactions", "{\"description\":\"transaction_id missing or has incorrect type.\"}"),
                Arguments.of("accounts", "{\"description\":\"account_id missing or has incorrect type.\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidPostTransactionRequests")
    void canHandleInvalidPostRequests(String contentType, String body, int expectedStatus, String expectedDescription) throws Exception {
        var requestBuilder = post("/transactions");
        requestBuilder.contentType(MediaType.parseMediaType(contentType));
        requestBuilder.content(body);

        var resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));

        if (expectedDescription != null) {
            resultActions.andExpect(content().string(expectedDescription));
        }
    }

    @ParameterizedTest
    @MethodSource("invalidPutTransactionRequests")
    void canHandleInvalidPutRequests(String contentType, String body, int expectedStatus, String expectedDescription) throws Exception {
        var requestBuilder = put("/transactions");
        requestBuilder.contentType(MediaType.parseMediaType(contentType));
        requestBuilder.content(body);

        var resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus));

        if (expectedDescription != null) {
            resultActions.andExpect(content().string(expectedDescription));
        }
    }

    private static Stream<Arguments> invalidPostTransactionRequests() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String accountId = UUID.randomUUID().toString();
        String transactionId = UUID.randomUUID().toString();
        String badRequestDescription = "{\"description\":\"Mandatory body parameters missing or have incorrect type.\"}";

        var missingTransactionId = new TestTransactionRequest(null, accountId, 7);
        var missingAccountId = new TestTransactionRequest(transactionId, null, 7);
        var missingAmount = new TestTransactionRequest(transactionId, accountId, null);
        var malformedUuid = new TestTransactionRequest(transactionId, "not-a-uuid", 7);
        var malformedTransactionId = new TestTransactionRequest("not-a-uuid", accountId, 7);
        var stringAmount = new TestTransactionRequest(transactionId, accountId, "high");

        return Stream.of(
                // 1. Wrong Content-Type
                Arguments.of(MediaType.APPLICATION_XML_VALUE, "<request></request>", 415, "{\"description\":\"Specified content type not allowed.\"}"),
                // 2. Missing transaction_id (SHOULD BE 201 AS IT IS OPTIONAL)
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(missingTransactionId), 201, null),
                // 3. Missing account_id
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(missingAccountId), 400, badRequestDescription),
                // 4. Missing amount
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(missingAmount), 400, badRequestDescription),
                // 5. Malformed UUID for account_id
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(malformedUuid), 400, badRequestDescription),
                // 6. Malformed UUID for transaction_id
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(malformedTransactionId), 400, badRequestDescription),
                // 7. String instead of integer for amount
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(stringAmount), 400, badRequestDescription)
        );
    }

    private static Stream<Arguments> invalidPutTransactionRequests() {
        return Stream.of(
                // 1. Wrong method (PUT on /transactions which only supports POST and GET)
                Arguments.of(MediaType.APPLICATION_JSON_VALUE, "{}", 405, "{\"description\":\"Specified HTTP method not allowed.\"}")
        );
    }
}
