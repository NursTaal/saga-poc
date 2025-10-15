package kg.demo.saga.orch.messaging;

import kg.demo.saga.contracts.command.CancelCommand;
import kg.demo.saga.contracts.command.ConfirmCommand;
import kg.demo.saga.contracts.transaction.DebitFailed;
import kg.demo.saga.contracts.transaction.MoneyDebited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

@Component
public class Listeners {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    private final RabbitTemplate amqp;
    private final String cmdEx;

    public Listeners(RabbitTemplate amqp, @Value("${app.exchanges.commands}") String cmdEx) {
        this.amqp = amqp;
        this.cmdEx = cmdEx;
    }

    @RabbitListener(queues = "orchestrator.on.debited.q")
    public void onDebited(MoneyDebited evt) {
        log.info("onDebited: {}", evt);
        amqp.convertAndSend(cmdEx, "cmd.confirm", new ConfirmCommand(evt.sagaId(), evt.txnId()));
    }

    @RabbitListener(queues = "orchestrator.on.debitFailed.q")
    public void onDebitFailed(DebitFailed evt) {
        log.info("onDebitFailed: {}", evt);
        amqp.convertAndSend(cmdEx, "cmd.cancel", new CancelCommand(evt.sagaId(), evt.txnId(), evt.reason()));
    }
}
