package com.demo.controller;
import com.demo.service.ScheduledJobs;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor
public class DemoController {
    private final ScheduledJobs jobs;
    private final JdbcTemplate jdbc;
    @GetMapping("/executions") public Map<String,Object> exec() { return Map.of("count",jobs.log2.size(),"log",jobs.log2); }
    @GetMapping("/locks") public List<Map<String,Object>> locks() { return jdbc.queryForList("SELECT * FROM shedlock"); }
}