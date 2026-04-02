package com.demo.controller;
import com.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j @RestController @RequestMapping("/api/users") @Profile("user")
public class UserController {
    private final Map<Long,User> users = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    public UserController() { add("Alice","alice@co.com"); add("Bob","bob@co.com"); add("Charlie","charlie@co.com"); }
    @GetMapping("/{id}") public User get(@PathVariable Long id) { log.info("GET /api/users/{}",id); User u=users.get(id); if(u==null) throw new RuntimeException("Not found"); return u; }
    @GetMapping public List<User> all() { return new ArrayList<>(users.values()); }
    private void add(String n,String e) { long id=seq.incrementAndGet(); users.put(id,new User(id,n,e)); }
}
