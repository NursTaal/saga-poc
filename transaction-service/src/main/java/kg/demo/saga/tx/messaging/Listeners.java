package kg.demo.saga.tx.messaging;

import kg.demo.saga.contracts.command.CancelCommand;
import kg.demo.saga.contracts.command.ConfirmCommand;
import kg.demo.saga.contracts.command.CreateTxnCommand;
import kg.demo.saga.contracts.transaction.DebitFailed;
import kg.demo.saga.contracts.transaction.MoneyDebited;
import kg.demo.saga.tx.service.TxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Listeners {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    private final TxService service;

    public Listeners(TxService s) {
        this.service = s;
    }

    // Orchestration: создание транзакции
    @RabbitListener(queues = "transaction.create.q")
    public void onCreate(CreateTxnCommand cmd) {
        service.createFromOrchestrator(cmd.sagaId(), cmd.txnId(), cmd.userId(), cmd.amount());
    }

    // Orchestration: подтверждение/отмена по командам
    @RabbitListener(queues = "transaction.confirm.q")
    public void onConfirm(ConfirmCommand cmd) {
        log.info("onConfirm: {}", cmd);
        service.confirm(cmd.sagaId(), cmd.txnId());
    }

    @RabbitListener(queues = "transaction.cancel.q")
    public void onCancel(CancelCommand cmd) {
        log.info("onCancel: {}", cmd);
        service.cancel(cmd.txnId());
    }

    // Choreography: реакция на события дебета
    @RabbitListener(queues = "transaction.on.debited.q")
    public void onMoneyDebited(MoneyDebited evt) {
        log.info("onMoneyDebited: {}", evt);
        service.confirm(evt.sagaId(), evt.txnId());
    }

    @RabbitListener(queues = "transaction.on.debitFailed.q")
    public void onDebitFailed(DebitFailed evt) {
        log.info("onDebitFailed: {}", evt);
        service.cancel(evt.txnId());
    }
}
