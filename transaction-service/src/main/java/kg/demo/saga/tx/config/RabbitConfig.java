package kg.demo.saga.tx.config;

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

    @Bean
    Queue confirmQ(@Value("${app.queues.confirm}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    Queue cancelQ(@Value("${app.queues.cancel}") String q) {
        return QueueBuilder.durable(q).build();
    }

    @Bean
    Binding confirmBind(@Qualifier("confirmQ") Queue confirmQ,
                        @Qualifier("commands") TopicExchange commands,
                        @Value("${app.routing.commands.confirm}") String rk) {

        return BindingBuilder.bind(confirmQ).to(commands).with(rk);
    }

    @Bean
    Binding cancelBind(@Qualifier("cancelQ") Queue cancelQ,
                       @Qualifier("commands") TopicExchange commands,
                       @Value("${app.routing.commands.cancel}") String rk) {

        return BindingBuilder.bind(cancelQ).to(commands).with(rk);
    }

    @Bean("createQ")
    Queue createQ() {
        return QueueBuilder.durable("transaction.create.q").build();
    }

    @Bean
    Binding createBind(@Qualifier("createQ") Queue createQ,
                       @Qualifier("commands") TopicExchange commands) {
        return BindingBuilder.bind(createQ).to(commands).with("cmd.create");
    }
}
