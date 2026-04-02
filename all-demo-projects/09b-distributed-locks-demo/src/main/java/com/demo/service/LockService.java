package com.demo.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j @Service
public class LockService {
    private final Map<String,ReentrantLock> locks = new ConcurrentHashMap<>();
    private final AtomicInteger stock = new AtomicInteger(100);
    private ReentrantLock getLock(String k) { return locks.computeIfAbsent(k, x->new ReentrantLock()); }

    public Map<String,Object> withLock(String pid, int qty) {
        ReentrantLock lock = getLock("inv:"+pid);
        try {
            if (!lock.tryLock(5,TimeUnit.SECONDS)) return Map.of("status","LOCK_TIMEOUT");
            if (stock.get()<qty) return Map.of("status","NO_STOCK","available",stock.get());
            Thread.sleep(100);
            stock.addAndGet(-qty);
            return Map.of("status","OK","remaining",stock.get());
        } catch(Exception e) { return Map.of("status","ERROR"); }
        finally { if(lock.isHeldByCurrentThread()) lock.unlock(); }
    }

    public Map<String,Object> withoutLock(String pid, int qty) {
        if (stock.get()<qty) return Map.of("status","NO_STOCK");
        try{Thread.sleep(100);}catch(Exception e){}
        stock.addAndGet(-qty);
        return Map.of("status","OK","remaining",stock.get(),"warning","NO LOCK!");
    }

    public int getStock() { return stock.get(); }
    public void reset() { stock.set(100); }
}