package kg.demo.saga.tx.api;
import kg.demo.saga.tx.service.TxService; import java.math.BigDecimal; import java.util.*; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/tx")
public class TxController {
  private final TxService service; public TxController(TxService s){this.service=s;}

  // Старт CHOREOGRAPHY: создаём txn и отсылаем TransactionCreated (через outbox)
  @PostMapping("/start-ch")
  public Map<String,Object> startCh(@RequestParam UUID userId, @RequestParam BigDecimal amount, @RequestParam(defaultValue="false") boolean failDebit){
    UUID sagaId = UUID.randomUUID(); UUID txnId = service.create(sagaId, userId, amount, failDebit);
    return Map.of("sagaId",sagaId,"txnId",txnId);
  }
}
