package com.example.mapper;

import com.example.model.User;
import com.example.model.dto.request.CreateUserRequest;
import com.example.model.dto.request.UpdateUserRequest;
import com.example.model.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreated_at())
                .build();
    }

    public User fromCreateRequest(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .created_at(LocalDateTime.now())
                .build();
    }

    public void updateFromRequest(UpdateUserRequest request, User user) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
    }
}
