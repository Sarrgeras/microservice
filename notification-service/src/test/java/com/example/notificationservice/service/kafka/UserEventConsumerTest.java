package com.example.notificationservice.service.kafka;

import com.example.notificationservice.dto.event.UserEvent;
import com.example.notificationservice.exception.EmailSendingException;
import com.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    void shouldProcessCreateEvent() {
        UserEvent event = new UserEvent("CREATE", "test@mail.ru", "John Doe");

        userEventConsumer.consumeUserEvent(event);

        verify(emailService).sendWelcomeEmail("test@mail.ru", "John Doe");
    }

    @Test
    void shouldProcessDeleteEvent() {
        UserEvent event = new UserEvent("DELETE", "test@mail.ru", "John Doe");

        userEventConsumer.consumeUserEvent(event);

        verify(emailService).sendGoodbyeEmail("test@mail.ru", "John Doe");
    }

    @Test
    void shouldLogWarningForUnknownEventType() {
        UserEvent event = new UserEvent("UNKNOWN", "test@mail.ru", "John Doe");

        userEventConsumer.consumeUserEvent(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldHandleEmailSendingFailure() {
        UserEvent event = new UserEvent("CREATE", "test@mail.ru", "John Doe");
        doThrow(new EmailSendingException("SMTP error", null))
                .when(emailService).sendWelcomeEmail(anyString(), anyString());

        assertDoesNotThrow(() -> userEventConsumer.consumeUserEvent(event));
    }
}
