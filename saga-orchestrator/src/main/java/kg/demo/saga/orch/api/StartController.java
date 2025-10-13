package kg.demo.saga.orch.api;
import kg.demo.saga.contracts.*; import org.springframework.web.bind.annotation.*; import org.springframework.amqp.rabbit.core.RabbitTemplate; import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal; import java.util.*;

@RestController @RequestMapping("/api/payments")
public class StartController {
  private final RabbitTemplate amqp; private final String cmdEx;
  public StartController(RabbitTemplate rt, @Value("${app.exchanges.commands}") String cmdEx){ this.amqp=rt; this.cmdEx=cmdEx; }

  // Orchestration entrypoint
  @PostMapping("/start")
  public Map<String,Object> start(@RequestParam UUID userId, @RequestParam BigDecimal amount, @RequestParam(defaultValue="false") boolean failDebit){
    UUID sagaId = UUID.randomUUID(); UUID txnId = UUID.randomUUID();
    amqp.convertAndSend(cmdEx, "cmd.debit", new DebitCommand(sagaId, txnId, userId, amount, failDebit));
    return Map.of("sagaId", sagaId, "txnId", txnId, "status", "STARTED");
  }
}
