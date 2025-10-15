package kg.demo.saga.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demo.saga.contracts.transaction.DebitFailed;
import kg.demo.saga.contracts.transaction.MoneyDebited;
import kg.demo.saga.wallet.domain.*;
import kg.demo.saga.wallet.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
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
            log.info("WalletService.handleDebit: sagaId={}, userId={}, txnId={}, amount={}, fail={}", sagaId, userId, txnId, amount, fail);
            if (fail) throw new IllegalStateException("FAIL_FLAG");
            var acc = repo.findById(userId).orElseGet(() -> repo.save(new WalletAccount(userId, new BigDecimal("0"))));
            acc.debit(amount);
            repo.save(acc);
            outbox.save(new OutboxEventEntity("Wallet", userId.toString(), "wallet.debited", om.convertValue(new MoneyDebited(sagaId, txnId, amount), JsonNode.class)));
            log.info("WalletService.handleDebit: SUCCESS sagaId={}, userId={}, txnId={}, amount={}", sagaId, userId, txnId, amount);
        } catch (Exception ex) {
            outbox.save(new OutboxEventEntity("Wallet", userId.toString(), "wallet.debitFailed", om.convertValue(new DebitFailed(sagaId, txnId, ex.getMessage()), JsonNode.class)));
        }
    }
}
