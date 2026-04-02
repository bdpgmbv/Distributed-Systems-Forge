package com.demo.controller;
import com.demo.model.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j @RestController @RequestMapping("/produce") @RequiredArgsConstructor
public class ProducerController {
    private final KafkaTemplate<String, Object> kafka;

    @PostMapping("/fire-forget")
    public Map<String,Object> fireAndForget() {
        OrderEvent e = newEvent();
        kafka.send("order-events", e);
        return Map.of("mode","FIRE_AND_FORGET","orderId",e.getOrderId(),"note","Sent. No confirmation.");
    }

    @PostMapping("/sync")
    public Map<String,Object> sync() throws Exception {
        OrderEvent e = newEvent();
        SendResult<String,Object> r = kafka.send("order-events", e.getOrderId(), e).get();
        return Map.of("mode","SYNCHRONOUS","orderId",e.getOrderId(),"partition",r.getRecordMetadata().partition(),"offset",r.getRecordMetadata().offset());
    }

    @PostMapping("/async")
    public Map<String,Object> async() {
        OrderEvent e = newEvent();
        kafka.send("order-events", e.getOrderId(), e).whenComplete((r,ex) -> {
            if (ex!=null) log.error("FAILED: {}",ex.getMessage());
            else log.info("Confirmed: p={} o={}",r.getRecordMetadata().partition(),r.getRecordMetadata().offset());
        });
        return Map.of("mode","ASYNC","orderId",e.getOrderId(),"note","Thread free. Callback fires later.");
    }

    @PostMapping("/with-key")
    public Map<String,Object> withKey(@RequestParam(defaultValue="C-42") String customerId) {
        OrderEvent e = newEvent(); e.setCustomerId(customerId);
        kafka.send("order-events", customerId, e);
        return Map.of("mode","KEYED","key",customerId,"note","Same key → same partition → ordered events.");
    }

    private OrderEvent newEvent() {
        return new OrderEvent("ORD-"+UUID.randomUUID().toString().substring(0,6),"C-"+(int)(Math.random()*100),BigDecimal.valueOf(Math.round(Math.random()*10000)/100.0),"CREATED");
    }
}
