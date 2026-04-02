package com.demo;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
@SpringBootApplication
public class KafkaConsumerApp {
    public static void main(String[] a) { SpringApplication.run(KafkaConsumerApp.class, a); }
    @Bean public NewTopic t() { return TopicBuilder.name("order-events").partitions(3).replicas(1).build(); }
}
