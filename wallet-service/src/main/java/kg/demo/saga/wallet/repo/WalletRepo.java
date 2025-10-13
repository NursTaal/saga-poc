package kg.demo.saga.wallet.repo;
import kg.demo.saga.wallet.domain.WalletAccount; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface WalletRepo extends JpaRepository<WalletAccount, UUID> {}
