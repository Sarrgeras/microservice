package com.example.service;

import com.example.exception.UserAlreadyExistsException;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.model.dto.event.UserEvent;
import com.example.model.dto.request.CreateUserRequest;
import com.example.model.dto.request.UpdateUserRequest;
import com.example.model.dto.response.UserResponse;
import com.example.repository.UserRepository;
import com.example.service.kafka.UserEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventService userEventService;

    public UserResponse createUser(CreateUserRequest userRequest) {
        log.info("Creating user with email: {}", userRequest.getEmail());

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("User with email {} already exists", userRequest.getEmail());
            throw new UserAlreadyExistsException(userRequest.getEmail());
        }

        User user = userMapper.fromCreateRequest(userRequest);
        User savedUser = userRepository.save(user);

        log.info("User saved to database. Creating Kafka event...");

        UserEvent event = new UserEvent("CREATE", savedUser.getEmail(), savedUser.getName());
        log.info("Event created: {}", event);


        userEventService.sendUserEvent(event);


        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new UserNotFoundException(id);
                });

        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest userRequest) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found for update with ID: {}", id);
                    return new UserNotFoundException(id);
                });

        if (userRequest.getEmail() != null &&
                !userRequest.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("Email {} already exists", userRequest.getEmail());
            throw new UserAlreadyExistsException(userRequest.getEmail());
        }

        userMapper.updateFromRequest(userRequest, user);
        User updatedUser = userRepository.save(user);

        log.info("User with ID: {} updated successfully", id);
        return userMapper.toResponse(updatedUser);
    }

    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException(id);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String userEmail = user.getEmail();
        String username = user.getName();


        userRepository.deleteById(id);

        UserEvent event = new UserEvent("DELETE", userEmail, username);
        userEventService.sendUserEvent(event);
        log.info("User with ID: {} deleted successfully", id);
    }
}
