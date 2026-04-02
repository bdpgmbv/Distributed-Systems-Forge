package com.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raw WebSocket — no STOMP, full control.
 * You handle session tracking and message routing.
 */
@Slf4j @Component
public class RawWsHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession s) throws Exception {
        sessions.put(s.getId(), s);
        log.info("🔌 Connected: {} (total: {})", s.getId(), sessions.size());
        broadcast("System: user joined (" + sessions.size() + " online)");
    }

    @Override
    protected void handleTextMessage(WebSocketSession s, TextMessage m) throws Exception {
        String msg = "User-" + s.getId().substring(0,4) + ": " + m.getPayload();
        log.info("📨 {}", msg);
        for (var e : sessions.entrySet()) {
            if (e.getValue().isOpen() && !e.getKey().equals(s.getId()))
                e.getValue().sendMessage(new TextMessage(msg));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession s, CloseStatus st) {
        sessions.remove(s.getId());
        log.info("🔌 Disconnected: {} (total: {})", s.getId(), sessions.size());
    }

    private void broadcast(String msg) {
        sessions.values().forEach(s -> { try { if(s.isOpen()) s.sendMessage(new TextMessage(msg)); } catch(Exception e){} });
    }
}
