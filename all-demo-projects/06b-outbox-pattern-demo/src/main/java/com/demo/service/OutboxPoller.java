package com.demo.service;
import com.demo.model.OutboxEvent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Slf4j @Component @RequiredArgsConstructor
public class OutboxPoller {
    private final EntityManager em;
    @Scheduled(fixedDelay=2000) @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = em.createQuery("FROM OutboxEvent WHERE published=false ORDER BY createdAt", OutboxEvent.class).getResultList();
        if (pending.isEmpty()) return;
        for (OutboxEvent e : pending) { log.info("📤 PUBLISHED: {}={}", e.getEventType(), e.getPayload()); e.setPublished(true); }
        log.info("📤 Published {} events", pending.size());
    }
}
