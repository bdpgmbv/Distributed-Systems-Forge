package com.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * STOMP Chat — broadcast + private messages + server push.
 */
@Slf4j @Controller @RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate msg;

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMsg broadcast(ChatMsg m) {
        m.setTimestamp(Instant.now().toString());
        log.info("💬 Broadcast: {} says '{}'", m.getFrom(), m.getText());
        return m;
    }

    @MessageMapping("/private")
    public void privateMsg(@Payload ChatMsg m) {
        m.setTimestamp(Instant.now().toString());
        log.info("🔒 Private: {} → {}", m.getFrom(), m.getTo());
        msg.convertAndSendToUser(m.getTo(), "/queue/private", m);
    }

    /** Server push — call via curl to push to all WebSocket clients */
    @GetMapping("/push")
    @ResponseBody
    public Map<String,String> push() {
        String text = "Server alert at " + Instant.now();
        msg.convertAndSend("/topic/alerts", new ChatMsg("SERVER", null, text, Instant.now().toString()));
        return Map.of("pushed", text);
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ChatMsg { private String from; private String to; private String text; private String timestamp; }
}
