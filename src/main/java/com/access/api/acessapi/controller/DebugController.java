package com.access.api.acessapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping("/mappings")
    public ResponseEntity<?> getMappings() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        
        handlerMethods.forEach((mapping, method) -> {
            log.info("Mapping: {} -> {}.{}", 
                mapping, 
                method.getBeanType().getSimpleName(), 
                method.getMethod().getName());
        });
        
        return ResponseEntity.ok("Check logs for mappings");
    }
    
    @GetMapping("/test-get/{id}")
    public ResponseEntity<String> testGet(@PathVariable Long id) {
        log.info("GET test endpoint called with id: {}", id);
        return ResponseEntity.ok("GET working for id: " + id);
    }
    
    @PutMapping("/test-put/{id}")
    public ResponseEntity<String> testPut(@PathVariable Long id, @RequestBody(required = false) String body) {
        log.info("PUT test endpoint called with id: {} and body: {}", id, body);
        return ResponseEntity.ok("PUT working for id: " + id);
    }
}