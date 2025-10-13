package kg.demo.saga.tx.repo;

import kg.demo.saga.tx.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
