package de.ecommerce.security.controllers;

import de.ecommerce.security.dto.LoginRequest;
import de.ecommerce.security.models.User;
import de.ecommerce.security.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling authentication and registration requests.
 * @author EgorBusuioc
 * 27.05.2025
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user with the provided details.
     *
     * @param user the user details for registration
     * @param bindingResult the result of the validation
     * @return a response entity with the registration status
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { // Checking if the user has any validation errors
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : errors) {
                builder.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(builder.toString());
        }

        try {
            authService.registerNewUser(user);
        }
        catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Logs in a user with the provided email and password.
     *
     * @param user the login request containing email and password
     * @return a response entity with the login status and JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user) {

        try {
            return ResponseEntity.ok(authService.findExistingUserByEmail(user));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }

    @GetMapping("/kafka/{userId}")
    public ResponseEntity<?> tryKafta(@PathVariable String userId) {
        try {
            authService.sendUserToKafka(userId);
            return ResponseEntity.ok("User sent to Kafka successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send user to Kafka");
        }
    }
}

