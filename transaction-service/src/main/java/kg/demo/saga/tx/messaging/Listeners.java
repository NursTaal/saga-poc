package kg.demo.saga.tx.messaging;
import kg.demo.saga.contracts.*; import kg.demo.saga.tx.service.TxService;
import org.springframework.amqp.rabbit.annotation.RabbitListener; import org.springframework.stereotype.Component;

@Component
public class Listeners {
  private final TxService service; public Listeners(TxService s){this.service=s;}

  // Orchestration: подтверждение/отмена по командам
  @RabbitListener(queues = "transaction.confirm.q")
  public void onConfirm(ConfirmCommand cmd){ service.confirm(cmd.sagaId(), cmd.txnId()); }

  @RabbitListener(queues = "transaction.cancel.q")
  public void onCancel(CancelCommand cmd){ service.cancel(cmd.txnId()); }

  // Choreography: реакция на события дебета
  @RabbitListener(queues = "transaction.on.debited.q")
  public void onMoneyDebited(MoneyDebited evt){ service.confirm(evt.sagaId(), evt.txnId()); }

  @RabbitListener(queues = "transaction.on.debitFailed.q")
  public void onDebitFailed(DebitFailed evt){ service.cancel(evt.txnId()); }
}
