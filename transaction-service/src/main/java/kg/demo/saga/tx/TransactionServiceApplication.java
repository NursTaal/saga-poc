package kg.demo.saga.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
