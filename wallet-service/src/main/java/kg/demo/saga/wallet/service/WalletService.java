package kg.demo.saga.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demo.saga.contracts.transaction.DebitFailed;
import kg.demo.saga.contracts.transaction.MoneyDebited;
import kg.demo.saga.wallet.domain.*;
import kg.demo.saga.wallet.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletService {
    private final WalletRepo repo;
    private final OutboxRepository outbox;
    private final ObjectMapper om;

    public WalletService(WalletRepo r, OutboxRepository o, ObjectMapper om) {
        this.repo = r;
        this.outbox = o;
        this.om = om;
    }

    @Transactional
    public void handleDebit(UUID sagaId, UUID userId, UUID txnId, BigDecimal amount, boolean fail) {
        try {
            if (fail) throw new IllegalStateException("FAIL_FLAG");
            var acc = repo.findById(userId).orElseGet(() -> repo.save(new WalletAccount(userId, new BigDecimal("0"))));
            acc.debit(amount);
            repo.save(acc);
            outbox.save(new OutboxEventEntity("Wallet", userId.toString(), "wallet.debited", om.writeValueAsString(new MoneyDebited(sagaId, txnId, amount))));
        } catch (Exception ex) {
            try {
                outbox.save(new OutboxEventEntity("Wallet", userId.toString(), "wallet.debitFailed", om.writeValueAsString(new DebitFailed(sagaId, txnId, ex.getMessage()))));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
