package com.demo.service;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
@Slf4j @Component
public class ScheduledJobs {
    public final CopyOnWriteArrayList<String> log2 = new CopyOnWriteArrayList<>();
    @Scheduled(fixedRate=10000) @SchedulerLock(name="heartbeat",lockAtLeastFor="8s",lockAtMostFor="9s")
    public void heartbeat() { String m="Heartbeat "+Instant.now(); log2.add(m); log.info("💓 {}",m); }
    @Scheduled(cron="0/30 * * * * *") @SchedulerLock(name="report",lockAtLeastFor="25s",lockAtMostFor="29s")
    public void report() { String m="Report "+Instant.now(); log2.add(m); log.info("📊 {}",m); try{Thread.sleep(3000);}catch(Exception e){} }
}