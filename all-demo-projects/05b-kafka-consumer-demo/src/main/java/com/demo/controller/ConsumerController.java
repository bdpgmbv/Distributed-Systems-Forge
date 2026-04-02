package com.demo.controller;
import com.demo.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j @RestController
public class ConsumerController {
    private final KafkaTemplate<String,Object> kafka;
    private final List<Map<String,Object>> consumed = new CopyOnWriteArrayList<>();

    public ConsumerController(KafkaTemplate<String,Object> k) { this.kafka = k; }

    @KafkaListener(topics="order-events", groupId="demo-group")
    public void consume(@Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("📩 Consumed: {} | partition={} offset={}", event.getOrderId(), partition, offset);
        consumed.add(Map.of("orderId",event.getOrderId(),"partition",partition,"offset",offset));
    }

    @PostMapping("/send")
    public Map<String,Object> send(@RequestParam(defaultValue="5") int count) {
        for (int i=0;i<count;i++) {
            String id="ORD-"+UUID.randomUUID().toString().substring(0,6);
            String cid="C-"+(i%3);
            kafka.send("order-events",cid,new OrderEvent(id,cid,BigDecimal.TEN,"CREATED"));
        }
        return Map.of("sent",count);
    }

    @GetMapping("/consumed")
    public Map<String,Object> getConsumed() { return Map.of("total",consumed.size(),"messages",consumed); }
}
