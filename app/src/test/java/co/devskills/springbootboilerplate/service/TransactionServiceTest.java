package co.devskills.springbootboilerplate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.devskills.springbootboilerplate.dto.account.AccountResponse;
import co.devskills.springbootboilerplate.dto.transaction.TransactionRequest;
import co.devskills.springbootboilerplate.dto.transaction.TransactionResponse;
import co.devskills.springbootboilerplate.entity.TransactionEntity;
import co.devskills.springbootboilerplate.repository.TransactionRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID accountId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
    }

    @ParameterizedTest
    @ValueSource(ints = {100, -50, 0})
    void createTransaction_ShouldSaveAndReturnResponse(int amount) {
        TransactionRequest request = new TransactionRequest(transactionId, accountId, amount);

        TransactionEntity entity = new TransactionEntity(transactionId, accountId, amount, OffsetDateTime.now(ZoneOffset.UTC));
        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(entity);

        TransactionResponse response = transactionService.create(request);

        assertEquals(transactionId, response.transactionId());
        assertEquals(accountId, response.accountId());
        assertEquals(amount, response.amount());
    }

    @Test
    void getAccountById_ShouldReturnBalance_WhenTransactionsExist() {
        when(transactionRepository.sumAmountByAccountId(accountId)).thenReturn(Optional.of(150));

        Optional<AccountResponse> response = transactionService.findAccountById(accountId);

        assertTrue(response.isPresent());
        assertEquals(accountId, response.get().accountId());
        assertEquals(150, response.get().balance());
    }

    @Test
    void getAccountById_ShouldReturnEmpty_WhenNoTransactionsExist() {
        when(transactionRepository.sumAmountByAccountId(accountId)).thenReturn(Optional.of(0));
        when(transactionRepository.findByAccountId(accountId)).thenReturn(Collections.emptyList());

        Optional<AccountResponse> response = transactionService.findAccountById(accountId);

        assertTrue(response.isEmpty());
    }
}
