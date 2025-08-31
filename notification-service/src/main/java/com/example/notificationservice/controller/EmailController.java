package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.EmailRequest;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        emailService.sendCustomEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getText()
        );
        return ResponseEntity.ok("Email sent successfully");
    }
}
