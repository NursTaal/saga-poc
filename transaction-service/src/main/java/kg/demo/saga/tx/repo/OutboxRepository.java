package kg.demo.saga.tx.repo;

import kg.demo.saga.tx.domain.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(String status);
}
