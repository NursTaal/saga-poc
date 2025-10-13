package kg.demo.saga.orch.messaging;
import kg.demo.saga.contracts.*; import org.springframework.stereotype.Component; import org.springframework.amqp.rabbit.annotation.RabbitListener; import org.springframework.amqp.rabbit.core.RabbitTemplate; import org.springframework.beans.factory.annotation.Value;

@Component
public class Listeners {
  private final RabbitTemplate amqp; private final String cmdEx;
  public Listeners(RabbitTemplate amqp, @Value("${app.exchanges.commands}") String cmdEx){ this.amqp=amqp; this.cmdEx=cmdEx; }

  @RabbitListener(queues = "orchestrator.on.debited.q")
  public void onDebited(MoneyDebited evt){
    amqp.convertAndSend(cmdEx, "cmd.confirm", new ConfirmCommand(evt.sagaId(), evt.txnId()));
  }

  @RabbitListener(queues = "orchestrator.on.debitFailed.q")
  public void onDebitFailed(DebitFailed evt){
    amqp.convertAndSend(cmdEx, "cmd.cancel", new CancelCommand(evt.sagaId(), evt.txnId(), evt.reason()));
  }
}
