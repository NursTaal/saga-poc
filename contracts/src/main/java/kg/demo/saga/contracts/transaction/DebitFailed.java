package kg.demo.saga.contracts.transaction;

import java.util.UUID;

public record DebitFailed(
        UUID sagaId,
        UUID txnId,
        String reason) {
}
