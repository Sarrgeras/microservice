package com.example.notificationservice.service;

import com.example.notificationservice.exception.EmailSendingException;
import com.example.notificationservice.model.UserEventType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Value("${spring.mail.from.address}")
    private String fromAddress;

    public void sendWelcomeEmail(String email, String username) {
        sendEmail(email, username, UserEventType.CREATE);
    }

    public void sendGoodbyeEmail(String email, String username) {
        sendEmail(email, username, UserEventType.DELETE);
    }

    private void sendEmail(String email, String username, UserEventType eventType) {
        var circuitBreaker = circuitBreakerFactory.create("emailService");

        circuitBreaker.run(() -> {
            try {
                String subject = eventType.getEmailSubject();
                String text = eventType.getEmailText()
                        .replace("{username}", username);

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(email);
                message.setSubject(subject);
                message.setText(text);

                mailSender.send(message);
                log.info("Email sent to: {} for event: {}", email, eventType.getEventType());
                return null;

            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", email, e.getMessage());
                throw new EmailSendingException("Failed to send email to " + email, e);
            }
        }, throwable -> {
            log.warn("Email service circuit breaker triggered: {}", throwable.getMessage());
            // Можно добавить fallback логику: запись в базу для повторной отправки
            return null;
        });
    }

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendCustomEmailFallback")
    public void sendCustomEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Custom email sent to: {} with subject: {}", toEmail, subject);

        } catch (Exception e) {
            log.error("Failed to send custom email to {}: {}", toEmail, e.getMessage());
            throw new EmailSendingException("Failed to send custom email to " + toEmail, e);
        }
    }

    private void sendCustomEmailFallback(String toEmail, String subject, String text, Throwable t) {
        log.warn("Fallback for custom email to {}: {}", toEmail, t.getMessage());
    }
}