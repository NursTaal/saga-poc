package kg.demo.saga.tx.api;

import kg.demo.saga.tx.service.TxService;

import java.math.BigDecimal;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tx")
public class TxController {
    private static final Logger log = LoggerFactory.getLogger(TxController.class);
    private final TxService service;

    public TxController(TxService s) {
        this.service = s;
    }

    // Старт CHOREOGRAPHY: создаём txn и отсылаем TransactionCreated (через outbox)
    @PostMapping("/start-ch")
    public Map<String, Object> startCh(@RequestParam("userId") UUID userId,
                                       @RequestParam("amount") BigDecimal amount,
                                       @RequestParam(name = "failDebit",defaultValue = "false") boolean failDebit) {
        log.info("startCh userId={}, amount={}", userId, amount);
        UUID sagaId = UUID.randomUUID();
        UUID txnId = service.create(sagaId, userId, amount, failDebit);
        log.info("startCh → sagaId={}, txnId={}", sagaId, txnId);
        return Map.of("sagaId", sagaId, "txnId", txnId);
    }
}
