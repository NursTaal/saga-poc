package kg.demo.saga.tx.messaging;

import kg.demo.saga.tx.domain.OutboxEventEntity;
import kg.demo.saga.tx.repo.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
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
                log.info("OutboxPublisher: sending event: {}", e.toString());
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
