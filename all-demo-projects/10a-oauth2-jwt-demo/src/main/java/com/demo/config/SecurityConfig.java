package com.demo.config;
import com.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.*;
@Configuration @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwt;
    @Bean public SecurityFilterChain chain(HttpSecurity h) throws Exception {
        return h.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers("/auth/**").permitAll().anyRequest().authenticated())
            .addFilterBefore(new OncePerRequestFilter(){
                protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws Exception {
                    String hdr=req.getHeader("Authorization");
                    if(hdr!=null&&hdr.startsWith("Bearer ")){
                        try{ var c=jwt.parse(hdr.substring(7)); List<String> roles=c.get("roles",List.class);
                            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject(),null,roles.stream().map(r->new SimpleGrantedAuthority("ROLE_"+r)).toList()));
                        }catch(Exception e){resp.sendError(401);return;}
                    }
                    chain.doFilter(req,resp);
                }
            }, UsernamePasswordAuthenticationFilter.class).build();
    }
}