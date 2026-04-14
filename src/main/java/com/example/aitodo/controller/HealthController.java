package com.example.aitodo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "AI Todo");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("timestamp", System.currentTimeMillis());
        return healthInfo;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}