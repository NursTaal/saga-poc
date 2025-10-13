package kg.demo.saga.tx.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;

@Entity @Table(name="transaction_entity")
public class TransactionEntity {
  @Id private UUID id; @Column(name="user_id") private UUID userId; private BigDecimal amount; private String status; @Column(name="created_at") private Instant createdAt;
  protected TransactionEntity(){}
  public TransactionEntity(UUID userId, BigDecimal amount){
    this.id = UUID.randomUUID(); this.userId = userId; this.amount = amount; this.status = "PENDING"; this.createdAt=Instant.now();
  }
  public UUID getId(){return id;} public UUID getUserId(){return userId;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;}
  public void confirm(){ this.status="CONFIRMED"; }
  public void cancel(){ this.status="CANCELLED"; }
}
