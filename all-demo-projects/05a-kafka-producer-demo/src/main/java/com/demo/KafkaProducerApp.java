package com.demo;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
@SpringBootApplication
public class KafkaProducerApp {
    public static void main(String[] a) { SpringApplication.run(KafkaProducerApp.class, a); }
    @Bean public NewTopic orderTopic() { return TopicBuilder.name("order-events").partitions(3).replicas(1).build(); }
}
