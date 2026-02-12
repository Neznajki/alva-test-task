package co.devskills.springbootboilerplate.service;

import co.devskills.springbootboilerplate.dto.account.AccountResponse;
import co.devskills.springbootboilerplate.dto.transaction.TransactionRequest;
import co.devskills.springbootboilerplate.dto.transaction.TransactionResponse;
import co.devskills.springbootboilerplate.entity.TransactionEntity;
import co.devskills.springbootboilerplate.repository.TransactionRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        TransactionEntity transactionEntity = new TransactionEntity(
                request.getTransactionId(),
                request.getAccountId(),
                request.getAmount(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );
        TransactionEntity saved = transactionRepository.save(transactionEntity);
        return mapToResponse(saved);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<TransactionResponse> getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .map(this::mapToResponse);
    }

    public Optional<AccountResponse> getAccountById(UUID accountId) {
        Integer balance = transactionRepository.sumAmountByAccountId(accountId);
        if (balance == null && transactionRepository.findByAccountId(accountId).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new AccountResponse(accountId, balance != null ? balance : 0));
    }

    private TransactionResponse mapToResponse(TransactionEntity transactionEntity) {
        return new TransactionResponse(
                transactionEntity.getTransactionId(),
                transactionEntity.getAccountId(),
                transactionEntity.getAmount(),
                transactionEntity.getCreatedAt()
        );
    }
}
