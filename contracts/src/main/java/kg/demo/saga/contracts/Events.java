package kg.demo.saga.contracts;
import java.math.BigDecimal; import java.util.UUID;

public record TransactionCreated(UUID sagaId, UUID txnId, UUID userId, BigDecimal amount, boolean failDebit) {}
public record MoneyDebited(UUID sagaId, UUID txnId, BigDecimal amount) {}
public record DebitFailed(UUID sagaId, UUID txnId, String reason) {}
public record TransactionConfirmed(UUID sagaId, UUID txnId) {}
