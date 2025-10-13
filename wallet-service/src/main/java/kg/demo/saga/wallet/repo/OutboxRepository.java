package kg.demo.saga.wallet.repo;
import kg.demo.saga.wallet.domain.OutboxEventEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID>{ List<OutboxEventEntity> findTop100ByStatusOrderByCreatedAtAsc(String status);} 
