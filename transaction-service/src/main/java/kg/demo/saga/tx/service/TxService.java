package kg.demo.saga.tx.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demo.saga.contracts.transaction.TransactionConfirmed;
import kg.demo.saga.contracts.transaction.TransactionCreated;
import kg.demo.saga.tx.domain.*;
import kg.demo.saga.tx.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.math.BigDecimal;

@Service
public class TxService {
    private static final Logger log = LoggerFactory.getLogger(TxService.class);
    private final TransactionRepository txRepo;
    private final OutboxRepository outbox;
    private final ObjectMapper om;

    public TxService(TransactionRepository txRepo, OutboxRepository outbox, ObjectMapper om) {
        this.txRepo = txRepo;
        this.outbox = outbox;
        this.om = om;
    }

    @Transactional
    public void createFromOrchestrator(UUID sagaId, UUID txnId, UUID userId, BigDecimal amount) {
        // конструктор с явным id
        var tx = new TransactionEntity(txnId, userId, amount, "PENDING");
        txRepo.save(tx);
        // (необязательно) положить в outbox событие TransactionCreated — если нужно
    }

//    CHOREOGRAPHY
    @Transactional
    public UUID create(UUID sagaId, UUID userId, BigDecimal amount, boolean failDebit) {
        log.info("Creating transaction for userId={}, amount={}, failDebit={}", userId, amount, failDebit);
        var tx = new TransactionEntity(userId, amount);
        txRepo.save(tx);
        try {
            var evt = new TransactionCreated(sagaId, tx.getId(), userId, amount, failDebit);
            outbox.save(new OutboxEventEntity("Transaction", tx.getId().toString(), "tx.created", om.convertValue(evt, JsonNode.class)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tx.getId();
    }

    @Transactional
    public void confirm(UUID sagaId, UUID txnId) {
        log.info("Confirming transaction, sagaId={} txnId={}", sagaId, txnId);
        var txOpt = txRepo.findById(txnId);
        if (txOpt.isEmpty()) {
            // Можно: лог + либо бросить исключение (остаточно), либо создать «заглушку» и подтвердить — но лучше создать заранее по CreateTxnCommand
            throw new NoSuchElementException("txn not found: " + txnId);
        }
        var tx = txOpt.get();
        tx.confirm();
        txRepo.save(tx);
        try {
            outbox.save(new OutboxEventEntity("Transaction", txnId.toString(), "tx.confirmed", om.convertValue(new TransactionConfirmed(sagaId, txnId), JsonNode.class)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void cancel(UUID txnId) {
        log.info("Canceling transaction, txnId={}", txnId);
        var tx = txRepo.findById(txnId).orElseThrow();
        tx.cancel();
        txRepo.save(tx);
    }
}
