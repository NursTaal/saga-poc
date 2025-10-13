package kg.demo.saga.wallet.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet_account")
public class WalletAccount {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    private BigDecimal balance;

    protected WalletAccount() {
    }

    public WalletAccount(UUID userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) throw new IllegalStateException("INSUFFICIENT_FUNDS");
        balance = balance.subtract(amount);
    }

    public void refund(BigDecimal amount) {
        balance = balance.add(amount);
    }
}
