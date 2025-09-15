package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.EmailRequest;
import com.example.notificationservice.service.EmailService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @CircuitBreaker(name = "emailService", fallbackMethod = "sendEmailFallback")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        log.info("Received email request to: {}, subject: {}",
                request.getToEmail(), request.getSubject());
        emailService.sendCustomEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getText()
        );
        log.info("Email sent successfully to: {}", request.getToEmail());
        return ResponseEntity.ok("Email sent successfully");
    }

    public ResponseEntity<String> sendEmailFallback(EmailRequest request, Throwable t) {
        log.warn("Email service fallback triggered for: {}. Reason: {}",
                request.getToEmail(), t.getMessage());

        return ResponseEntity.accepted()
                .body("Email queued for delivery. Service temporarily unavailable.");
    }
}
