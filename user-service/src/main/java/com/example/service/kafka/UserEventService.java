package com.example.service.kafka;

import com.example.model.dto.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void sendUserEvent(UserEvent event) {
        try {
            if (kafkaTemplate == null) {
                log.error("KafkaTemplate is NULL! Check configuration");
                return;
            }
            log.info("Attempting to send event to Kafka topic: {}", TOPIC);
            log.info("Event content: {}", event);

            kafkaTemplate.send(TOPIC, event);
            log.info("Event sent to Kafka successfully");

        } catch (Exception e) {
            log.error("Failed to send event to Kafka: {}", e.getMessage(), e);
        }
    }
}
