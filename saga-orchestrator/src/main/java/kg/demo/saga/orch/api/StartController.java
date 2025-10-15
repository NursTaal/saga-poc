package kg.demo.saga.orch.api;

import kg.demo.saga.contracts.command.CreateTxnCommand;
import kg.demo.saga.contracts.command.DebitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
public class StartController {
    private static final Logger log = LoggerFactory.getLogger(StartController.class);
    private final RabbitTemplate amqp;
    private final String cmdEx;

    public StartController(RabbitTemplate rt, @Value("${app.exchanges.commands}") String cmdEx) {
        this.amqp = rt;
        this.cmdEx = cmdEx;
    }

    // Orchestration entrypoint
    @PostMapping("/start")
    public Map<String, Object> start(@RequestParam("userId") UUID userId,
                                     @RequestParam("amount") BigDecimal amount,
                                     @RequestParam(name = "failDebit", defaultValue = "false") boolean failDebit) {
        UUID sagaId = UUID.randomUUID();
        UUID txnId = UUID.randomUUID();
        log.info("start userId={}, amount={}, failDebit={} sagaId={}, txnId={}", userId, amount, failDebit, sagaId, txnId);
        // start the saga by sending the first command
        amqp.convertAndSend(cmdEx, "cmd.create", new CreateTxnCommand(sagaId, txnId, userId, amount));
        amqp.convertAndSend(cmdEx, "cmd.debit",  new DebitCommand(sagaId, txnId, userId, amount, failDebit));
        return Map.of("sagaId", sagaId, "txnId", txnId, "status", "STARTED");
    }
}
