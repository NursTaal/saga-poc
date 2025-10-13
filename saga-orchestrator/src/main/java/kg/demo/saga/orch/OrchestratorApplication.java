package kg.demo.saga.orch;
import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.boot.SpringApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
@SpringBootApplication @EnableRabbit
public class OrchestratorApplication { public static void main(String[] args){ SpringApplication.run(OrchestratorApplication.class,args);} }
