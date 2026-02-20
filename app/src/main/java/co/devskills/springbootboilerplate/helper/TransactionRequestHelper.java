package co.devskills.springbootboilerplate.helper;

import co.devskills.springbootboilerplate.dto.transaction.TransactionRequest;

import java.util.UUID;

public class TransactionRequestHelper {
    public static TransactionRequest withGeneratedIdIfMissing(TransactionRequest incomingRequest) {
        if (incomingRequest.transactionId() != null) {
            return incomingRequest;
        }
        return new TransactionRequest(UUID.randomUUID(), incomingRequest.accountId(), incomingRequest.amount());
    }
}
