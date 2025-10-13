package kg.demo.saga.tx.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.demo.saga.contracts.transaction.TransactionConfirmed;
import kg.demo.saga.contracts.transaction.TransactionCreated;
import kg.demo.saga.tx.domain.*; import kg.demo.saga.tx.repo.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*; import java.math.BigDecimal;

@Service
public class TxService {
  private final TransactionRepository txRepo; private final OutboxRepository outbox; private final ObjectMapper om;
  public TxService(TransactionRepository txRepo, OutboxRepository outbox, ObjectMapper om){this.txRepo=txRepo; this.outbox=outbox; this.om=om;}

  @Transactional
  public UUID create(UUID sagaId, UUID userId, BigDecimal amount, boolean failDebit){
    var tx = new TransactionEntity(userId, amount); txRepo.save(tx);
    try {
      var evt = new TransactionCreated(sagaId, tx.getId(), userId, amount, failDebit);
      outbox.save(new OutboxEventEntity("Transaction", tx.getId().toString(), "tx.created", om.writeValueAsString(evt)));
    } catch (Exception e) { throw new RuntimeException(e); }
    return tx.getId();
  }

  @Transactional public void confirm(UUID sagaId, UUID txnId){
    var tx = txRepo.findById(txnId).orElseThrow(); tx.confirm(); txRepo.save(tx);
    try {
      outbox.save(new OutboxEventEntity("Transaction", txnId.toString(), "tx.confirmed", om.writeValueAsString(new TransactionConfirmed(sagaId, txnId))));
    } catch(Exception e){ throw new RuntimeException(e);} }

  @Transactional public void cancel(UUID txnId){ var tx = txRepo.findById(txnId).orElseThrow(); tx.cancel(); txRepo.save(tx); }
}
