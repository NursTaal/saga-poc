package kg.demo.saga.contracts.command;

import java.util.UUID;

public record CancelCommand(
        UUID sagaId,
        UUID txnId,
        String reason) {
}
