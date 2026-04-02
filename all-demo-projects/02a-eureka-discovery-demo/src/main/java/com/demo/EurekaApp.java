package com.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication @EnableEurekaServer @EnableFeignClients
public class EurekaApp { public static void main(String[] a) { SpringApplication.run(EurekaApp.class, a); } }
