package kg.demo.saga.contracts.transaction;

import java.util.UUID;


public record TransactionConfirmed(
        UUID sagaId,
        UUID txnId) {
}
