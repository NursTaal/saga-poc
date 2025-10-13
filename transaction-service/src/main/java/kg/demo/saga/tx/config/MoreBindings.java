package kg.demo.saga.tx.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;

@Configuration
public class MoreBindings {
    @Bean
    Queue onDebitedQ() {
        return QueueBuilder.durable("transaction.on.debited.q").build();
    }

    @Bean
    Queue onDebitFailedQ() {
        return QueueBuilder.durable("transaction.on.debitFailed.q").build();
    }

    @Bean
    Binding onDebitedBind(Queue onDebitedQ, TopicExchange events) {
        return BindingBuilder.bind(onDebitedQ).to(events).with("wallet.debited");
    }

    @Bean
    Binding onDebitFailedBind(Queue onDebitFailedQ, TopicExchange events) {
        return BindingBuilder.bind(onDebitFailedQ).to(events).with("wallet.debitFailed");
    }
}
