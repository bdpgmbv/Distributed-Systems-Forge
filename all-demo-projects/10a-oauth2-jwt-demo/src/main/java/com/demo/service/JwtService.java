package com.demo.service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Service
public class JwtService {
    private final SecretKey key;
    private final long exp;
    public JwtService(@Value("${app.jwt.secret}") String s, @Value("${app.jwt.expiration-ms}") long e) { key=Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8)); exp=e; }
    public String generate(String user, List<String> roles) { return Jwts.builder().subject(user).claim("roles",roles).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+exp)).signWith(key).compact(); }
    public Claims parse(String t) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(t).getPayload(); }
}