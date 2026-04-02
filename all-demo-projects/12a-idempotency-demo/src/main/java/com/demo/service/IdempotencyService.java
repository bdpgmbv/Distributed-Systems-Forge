package com.demo.service;
import com.demo.model.IdempotencyRecord;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

@Slf4j @Service @RequiredArgsConstructor
public class IdempotencyService {
    private final EntityManager em;

    @Transactional(readOnly=true)
    public Optional<IdempotencyRecord> find(String key) {
        return Optional.ofNullable(em.find(IdempotencyRecord.class, key));
    }

    @Transactional
    public void save(String key, String body, int status) {
        em.persist(IdempotencyRecord.builder().idempotencyKey(key).responseBody(body).responseStatus(status).createdAt(Instant.now()).build());
        log.info("💾 Stored idempotency record for key: {}", key);
    }
}