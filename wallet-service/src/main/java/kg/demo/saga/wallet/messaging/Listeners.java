package kg.demo.saga.wallet.messaging;

import kg.demo.saga.contracts.command.DebitCommand;
import kg.demo.saga.contracts.transaction.TransactionCreated;
import kg.demo.saga.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
public class Listeners {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    private final WalletService service;

    public Listeners(WalletService s) {
        this.service = s;
    }

    // Orchestration: команда на дебет
    @RabbitListener(queues = "wallet.debit.q")
    public void onDebit(DebitCommand cmd) {
        log.info("Received debit command {}", cmd);
        service.handleDebit(cmd.sagaId(), cmd.userId(), cmd.txnId(), cmd.amount(), cmd.failDebit());
    }

    // Choreography: реагируем на созданную транзакцию
    @RabbitListener(queues = "wallet.on.txCreated.q")
    public void onTxCreated(TransactionCreated evt) {
        log.info("Received transaction created event {}", evt);
        service.handleDebit(evt.sagaId(), evt.userId(), evt.txnId(), evt.amount(), evt.failDebit());
    }
}
