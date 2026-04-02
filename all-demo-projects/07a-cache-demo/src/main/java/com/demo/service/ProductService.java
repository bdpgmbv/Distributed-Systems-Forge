package com.demo.service;
import com.demo.model.Product;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j @Service @RequiredArgsConstructor
public class ProductService {
    private final EntityManager em;

    @Bean CommandLineRunner seed() {
        return a -> {
            em.persist(Product.builder().name("Laptop").price(new BigDecimal("999.99")).category("Electronics").build());
            em.persist(Product.builder().name("Phone").price(new BigDecimal("699.99")).category("Electronics").build());
            em.persist(Product.builder().name("Book").price(new BigDecimal("29.99")).category("Books").build());
        };
    }

    @Cacheable(value="products", key="#id")
    @Transactional(readOnly=true)
    public Product getProduct(Long id) {
        log.info("🗄️ DB QUERY for product {} (this should only happen on cache MISS)", id);
        simulateSlowDb();
        return em.find(Product.class, id);
    }

    @CachePut(value="products", key="#id")
    @Transactional
    public Product updateProduct(Long id, String name, BigDecimal price) {
        Product p = em.find(Product.class, id);
        p.setName(name); p.setPrice(price);
        log.info("📝 Updated product {} — cache also updated via @CachePut", id);
        return p;
    }

    @CacheEvict(value="products", key="#id")
    @Transactional
    public void deleteProduct(Long id) {
        em.remove(em.find(Product.class, id));
        log.info("🗑️ Deleted product {} — cache evicted", id);
    }

    @CacheEvict(value="products", allEntries=true)
    public void clearCache() { log.info("🧹 ENTIRE product cache cleared"); }

    private void simulateSlowDb() { try { Thread.sleep(500); } catch (Exception e) {} }
}
