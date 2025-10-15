package kg.demo.saga.orch.config;

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

    @Bean("onDebited")
    Queue onDebited(@Value("${app.queues.onDebited}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean("onDebitFailed")
    Queue onDebitFailed(@Value("${app.queues.onDebitFailed}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    Binding b1(@Qualifier("onDebited") Queue onDebited,
               @Qualifier("events") TopicExchange events,
               @Value("${app.routing.events.debited}") String rk) {

        return BindingBuilder.bind(onDebited).to(events).with(rk);
    }

    @Bean
    Binding b2(@Qualifier("onDebitFailed") Queue onDebitFailed,
               @Qualifier("events") TopicExchange events,
               @Value("${app.routing.events.debitFailed}") String rk) {
        return BindingBuilder.bind(onDebitFailed).to(events).with(rk);
    }
}
