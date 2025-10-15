package kg.demo.saga.tx.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Configuration
public class MoreBindings {
    @Bean("txOnDebited")
    Queue onDebitedQ() {
        return QueueBuilder.durable("transaction.on.debited.q").build();
    }

    @Bean("txOnDebitFailed")
    Queue onDebitFailedQ() {
        return QueueBuilder.durable("transaction.on.debitFailed.q").build();
    }

    @Bean
    Binding onDebitedBind(@Qualifier("txOnDebited") Queue onDebitedQ,
                          @Qualifier("events") TopicExchange events) {
        return BindingBuilder.bind(onDebitedQ).to(events).with("wallet.debited");
    }

    @Bean
    Binding onDebitFailedBind(@Qualifier("txOnDebitFailed")Queue onDebitFailedQ,
                              @Qualifier("events") TopicExchange events) {
        return BindingBuilder.bind(onDebitFailedQ).to(events).with("wallet.debitFailed");
    }
}
