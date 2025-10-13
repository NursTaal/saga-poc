package kg.demo.saga.contracts.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCreated(
        UUID sagaId,
        UUID txnId,
        UUID userId,
        BigDecimal amount,
        boolean failDebit) {
}
