package kg.demo.saga.contracts.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTxnCommand(
        UUID sagaId,
        UUID txnId,
        UUID userId,
        BigDecimal amount) {
}