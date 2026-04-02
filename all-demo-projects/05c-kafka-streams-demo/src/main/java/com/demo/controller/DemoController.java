package com.demo.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j @RestController @RequiredArgsConstructor
public class DemoController {
    private final KafkaTemplate<String,String> kafka;
    private final StreamsBuilderFactoryBean factory;

    @PostMapping("/send")
    public Map<String,Object> send(@RequestParam String text) {
        kafka.send("words-input", text);
        return Map.of("sent",text);
    }

    @GetMapping("/counts")
    public Map<String,Object> counts() {
        ReadOnlyKeyValueStore<String,Long> s = factory.getKafkaStreams()
            .store(StoreQueryParameters.fromNameAndType("word-counts-store",QueryableStoreTypes.keyValueStore()));
        Map<String,Long> m = new TreeMap<>(); s.all().forEachRemaining(kv->m.put(kv.key,kv.value));
        return Map.of("wordCounts",m);
    }

    @GetMapping("/counts/{word}")
    public Map<String,Object> count(@PathVariable String word) {
        ReadOnlyKeyValueStore<String,Long> s = factory.getKafkaStreams()
            .store(StoreQueryParameters.fromNameAndType("word-counts-store",QueryableStoreTypes.keyValueStore()));
        Long c = s.get(word.toLowerCase());
        return Map.of("word",word,"count",c!=null?c:0);
    }
}
