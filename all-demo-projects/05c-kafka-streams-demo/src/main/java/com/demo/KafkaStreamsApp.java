package com.demo;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
@SpringBootApplication @EnableKafkaStreams
public class KafkaStreamsApp {
    public static void main(String[] a) { SpringApplication.run(KafkaStreamsApp.class, a); }
    @Bean public NewTopic in() { return TopicBuilder.name("words-input").partitions(1).replicas(1).build(); }
}
