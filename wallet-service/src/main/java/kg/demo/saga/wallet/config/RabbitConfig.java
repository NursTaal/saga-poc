package kg.demo.saga.wallet.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitConfig {
    @Bean("events")
    TopicExchange events(@Value("${app.exchanges.events}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean("commands")
    TopicExchange commands(@Value("${app.exchanges.commands}") String name) {
        return new TopicExchange(name, true, false);
    }

    @Bean("walletDebitQ")
    Queue debitQ(@Value("${app.queues.debit}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean("walletOnTxCreatedQ")
    Queue onTxCreatedQ(@Value("${app.queues.onTxCreated}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    Binding debitBind(@Qualifier("walletDebitQ") Queue debitQ,
                      @Qualifier("commands") TopicExchange commands,
                      @Value("${app.routing.commands.debit}") String rk) {
        return BindingBuilder.bind(debitQ).to(commands).with(rk);
    }

    @Bean
    Binding onTxCreatedBind(@Qualifier("walletOnTxCreatedQ") Queue onTxCreatedQ,
                            @Qualifier("events") TopicExchange events,
                            @Value("${app.routing.events.txCreated}") String rk) {
        return BindingBuilder.bind(onTxCreatedQ).to(events).with(rk);
    }
}
