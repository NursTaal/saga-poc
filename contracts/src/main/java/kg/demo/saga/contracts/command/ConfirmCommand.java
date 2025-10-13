package kg.demo.saga.contracts.command;

import java.util.UUID;

public record ConfirmCommand(
        UUID sagaId,
        UUID txnId) {
}
