package com.demo.config;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Slf4j @Configuration
public class WordCountStream {
    @Bean
    public KStream<String,String> stream(StreamsBuilder builder) {
        KStream<String,String> src = builder.stream("words-input", Consumed.with(Serdes.String(),Serdes.String()));
        src.flatMapValues(v -> Arrays.asList(v.toLowerCase().split("\\W+")))
            .filter((k,v) -> !v.isEmpty())
            .groupBy((k,v)->v, Grouped.with(Serdes.String(),Serdes.String()))
            .count(Materialized.as("word-counts-store"))
            .toStream().peek((w,c) -> log.info("📊 {}={}", w, c));
        return src;
    }
}
