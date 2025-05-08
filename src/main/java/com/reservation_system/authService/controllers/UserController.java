package com.reservation_system.authService.controllers;

import com.reservation_system.authService.Services.UserService;
import com.reservation_system.authService.models.User;
import com.reservation_system.authService.payload.request.UpdateUserRequest;
import com.reservation_system.authService.payload.response.MessageResponse;
import com.reservation_system.authService.payload.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getUsers();

        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles().stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User updatedUser = userService.updateUser(
                id,
                updateUserRequest.getUsername(),
                updateUserRequest.getEmail(),
                updateUserRequest.getPassword(),
                updateUserRequest.getRoles()
        );

        UserResponse userResponse = new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );

        return ResponseEntity.ok(userResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(@PathVariable long id) {
        return ResponseEntity.ok(new MessageResponse("hello"));
    }
}
