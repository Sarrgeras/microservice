package com.example.notificationservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserEventType {
    CREATE(
            "CREATE",
            "Welcome to Our Service!",
            "{username}, welcome to our platform! Your account has been successfully created."
    ),
    DELETE(
            "DELETE",
            "Account Deletion Confirmation",
            "{username}, your account has been deleted. We're sorry to see you go."
    );

    private final String eventType;
    private final String emailSubject;
    private final String emailText;

    public static UserEventType fromString(String eventType) {
        for (UserEventType type : values()) {
            if (type.eventType.equalsIgnoreCase(eventType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + eventType);
    }
}