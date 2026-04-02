package com.demo.controller;
import com.demo.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.*;
@RestController @RequiredArgsConstructor
public class DemoController {
    private final LockService svc;
    @GetMapping("/stock") public Map<String,Object> stock() { return Map.of("stock",svc.getStock()); }
    @PostMapping("/reset") public Map<String,Object> reset() { svc.reset(); return Map.of("stock",100); }

    @PostMapping("/race/safe") public Map<String,Object> safe() throws Exception {
        svc.reset(); ExecutorService ex=Executors.newFixedThreadPool(20);
        List<Future<Map<String,Object>>> f=new ArrayList<>();
        for(int i=0;i<20;i++) f.add(ex.submit(()->svc.withLock("P1",5)));
        f.forEach(x->{try{x.get();}catch(Exception e){}});
        ex.shutdown();
        return Map.of("finalStock",svc.getStock(),"expected","Should be 0 (correct)");
    }

    @PostMapping("/race/unsafe") public Map<String,Object> unsafe() throws Exception {
        svc.reset(); ExecutorService ex=Executors.newFixedThreadPool(20);
        List<Future<Map<String,Object>>> f=new ArrayList<>();
        for(int i=0;i<20;i++) f.add(ex.submit(()->svc.withoutLock("P1",5)));
        f.forEach(x->{try{x.get();}catch(Exception e){}});
        ex.shutdown();
        return Map.of("finalStock",svc.getStock(),"expected","Likely NEGATIVE (oversold!)");
    }
}