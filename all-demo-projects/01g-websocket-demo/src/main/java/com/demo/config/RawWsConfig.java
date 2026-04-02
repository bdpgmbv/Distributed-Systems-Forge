package com.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration @EnableWebSocket @RequiredArgsConstructor
public class RawWsConfig implements WebSocketConfigurer {
    private final RawWsHandler handler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry r) {
        r.addHandler(handler, "/ws/raw").setAllowedOrigins("*");
    }
}
