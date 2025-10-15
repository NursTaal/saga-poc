package kg.demo.saga.wallet.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEventEntity {
    @Id
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String type;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode  headers;
    private String status;
    private Integer attempts;
    private Instant createdAt;
    private String lastError;

    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(String aggType, String aggId, String type, JsonNode payload) {
        this.id = UUID.randomUUID();
        this.aggregateType = aggType;
        this.aggregateId = aggId;
        this.type = type;
        this.payload = payload;
        this.status = "NEW";
        this.attempts = 0;
        this.createdAt = Instant.now();
    }

    public String getType() {
        return type;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer a) {
        this.attempts = a;
    }

    public void setLastError(String e) {
        this.lastError = e;
    }
}
