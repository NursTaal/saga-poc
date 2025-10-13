package kg.demo.saga.wallet.messaging;

import kg.demo.saga.wallet.repo.OutboxRepository;
import kg.demo.saga.wallet.domain.OutboxEventEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Component
public class OutboxPublisher {
    private final OutboxRepository repo;
    private final RabbitTemplate amqp;
    private final String eventsEx;

    public OutboxPublisher(OutboxRepository repo, RabbitTemplate amqp, @Value("${app.exchanges.events}") String eventsEx) {
        this.repo = repo;
        this.amqp = amqp;
        this.eventsEx = eventsEx;
    }

    @Scheduled(fixedDelay = 200)
    public void publish() {
        List<OutboxEventEntity> batch = repo.findTop100ByStatusOrderByCreatedAtAsc("NEW");
        for (var e : batch) {
            try {
                amqp.convertAndSend(eventsEx, e.getType(), e.getPayload());
                e.setStatus("SENT");
            } catch (Exception ex) {
                e.setAttempts(e.getAttempts() + 1);
                e.setLastError(ex.getMessage());
                if (e.getAttempts() > 10) e.setStatus("FAIL");
            }
        }
        repo.saveAll(batch);
    }
}
