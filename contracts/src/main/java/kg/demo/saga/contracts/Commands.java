package kg.demo.saga.contracts;
import java.math.BigDecimal; import java.util.UUID;

public record DebitCommand(UUID sagaId, UUID txnId, UUID userId, BigDecimal amount, boolean failDebit) {}
public record ConfirmCommand(UUID sagaId, UUID txnId) {}
public record CancelCommand(UUID sagaId, UUID txnId, String reason) {}
