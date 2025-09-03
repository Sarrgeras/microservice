package com.example.notificationservice.service.kafka;

import com.example.notificationservice.dto.event.UserEvent;
import com.example.notificationservice.exception.EmailSendingException;
import com.example.notificationservice.model.UserEventType;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "${kafka.group.notification-group}")
    public void consumeUserEvent(UserEvent event) {
        log.info("Received user event: {}", event);

        try {
            UserEventType eventType = UserEventType.fromString(event.getEventType());

            switch (eventType) {
                case CREATE:
                    emailService.sendWelcomeEmail(event.getEmail(), event.getUsername());
                    break;
                case DELETE:
                    emailService.sendGoodbyeEmail(event.getEmail(), event.getUsername());
                    break;
                default:
                    log.warn("⚠️ Unknown event type: {}", event.getEventType());
            }

        } catch (IllegalArgumentException e) {
            log.warn("⚠Unknown event type: {}", event.getEventType());
        } catch (EmailSendingException e) {
            log.error("Failed to send email for event {}: {}", event.getEventType(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error processing event: {}", e.getMessage(), e);
        }
    }
}