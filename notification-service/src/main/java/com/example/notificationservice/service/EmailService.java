package com.example.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from.address}")
    private String fromAddress;

    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "Добро пожаловать!";
        String text = String.format("""
            Здравствуйте, %s!
            
            Ваш аккаунт на сайте example.com был успешно создан.
            
            С уважением,
            Команда Example.com
            """, username);

        sendEmail(toEmail, subject, text);
    }

    public void sendGoodbyeEmail(String toEmail, String username) {
        String subject = "Ваш аккаунт удален";
        String text = String.format("""
            Здравствуйте, %s!
            
            Ваш аккаунт был удалён.
            
            С уважением,
            Команда Example.com
            """, username);

        sendEmail(toEmail, subject, text);
    }

    public void sendCustomEmail(String toEmail, String subject, String text) {
        sendEmail(toEmail, subject, text);
    }

    private void sendEmail(String toEmail, String subject, String text) {
        try {
            log.info("Attempting to send email from: {}", fromAddress);
            log.info("Using mail sender: {}", mailSender);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}