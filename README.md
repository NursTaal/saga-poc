# Saga PoC (Java 21 / Spring Boot 3.3 / RabbitMQ / Postgres)

Два варианта согласованности: **Orchestration** и **Choreography**, Outbox, компенсации.

## Что внутри
- `contracts/` — общие события/команды (Java records)
- `transaction-service/` — транзакции, Outbox, подтверждение/отмена + API для Choreography
- `wallet-service/` — кошелёк, дебет/фейл + Outbox
- `saga-orchestrator/` — оркестратор (команды и реакции на события)
- `docker/` — RabbitMQ + Postgres (3 БД), `docker-compose.yml`

## Быстрый старт
```bash
cd docker
docker compose up -d
cd ..
mvn -q -DskipTests clean package

# три терминала
java -jar transaction-service/target/transaction-service-1.0.0.jar
java -jar wallet-service/target/wallet-service-1.0.0.jar
java -jar saga-orchestrator/target/saga-orchestrator-1.0.0.jar
```

### Orchestration — happy path
```bash
curl -s -X POST "http://localhost:8080/api/payments/start?userId=11111111-1111-1111-1111-111111111111&amount=500&failDebit=false" | jq
```

### Orchestration — ошибка на дебете (компенсация)
```bash
curl -s -X POST "http://localhost:8080/api/payments/start?userId=11111111-1111-1111-1111-111111111111&amount=500000&failDebit=true" | jq
```

### Choreography — happy path
```bash
curl -s -X POST "http://localhost:8081/api/tx/start-ch?userId=11111111-1111-1111-1111-111111111111&amount=700&failDebit=false" | jq
```

### Choreography — ошибка на дебете
```bash
curl -s -X POST "http://localhost:8081/api/tx/start-ch?userId=11111111-1111-1111-1111-111111111111&amount=999999&failDebit=true" | jq
```

## RabbitMQ UI
- http://localhost:15672 (guest/guest)
- Exchanges: `domain.evt`, `saga.cmd`
- Queues: `wallet.debit.q`, `wallet.on.txCreated.q`, `transaction.confirm.q`, `transaction.cancel.q`, `transaction.on.debited.q`, `transaction.on.debitFailed.q`, `orchestrator.on.debited.q`, `orchestrator.on.debitFailed.q`
