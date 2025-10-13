package kg.demo.saga.tx.config;
import org.springframework.amqp.core.*; import org.springframework.context.annotation.*; import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitConfig {
  @Bean TopicExchange events(@Value("${app.exchanges.events}") String name){ return new TopicExchange(name,true,false); }
  @Bean TopicExchange commands(@Value("${app.exchanges.commands}") String name){ return new TopicExchange(name,true,false); }

  @Bean Queue confirmQ(@Value("${app.queues.confirm}") String q){ return QueueBuilder.durable(q).build(); }
  @Bean Queue cancelQ(@Value("${app.queues.cancel}") String q){ return QueueBuilder.durable(q).build(); }

  @Bean Binding confirmBind(Queue confirmQ, TopicExchange commands, @Value("${app.routing.commands.confirm}") String rk){ return BindingBuilder.bind(confirmQ).to(commands).with(rk); }
  @Bean Binding cancelBind(Queue cancelQ, TopicExchange commands, @Value("${app.routing.commands.cancel}") String rk){ return BindingBuilder.bind(cancelQ).to(commands).with(rk); }
}
