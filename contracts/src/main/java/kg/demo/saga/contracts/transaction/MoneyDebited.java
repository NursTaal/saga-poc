package kg.demo.saga.contracts.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record MoneyDebited(
        UUID sagaId,
        UUID txnId,
        BigDecimal amount) {
}