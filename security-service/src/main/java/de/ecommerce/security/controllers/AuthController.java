package de.ecommerce.security.controllers;

import de.ecommerce.security.dto.LoginRequest;
import de.ecommerce.security.dto.RegistrationDTO;
import de.ecommerce.security.dto.ResetPassword;
import de.ecommerce.security.dto.ResetPasswordRequest;
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
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationDTO user, BindingResult bindingResult) {

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
        catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        return ResponseEntity.ok("Activation link sent to your email. " +
                "Please check your inbox and activate your account.");
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
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateUser(@RequestParam String token) {
        try {
            authService.validateActivationToken(token);
            return ResponseEntity.ok("User activated successfully, now you can login");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to activate user");
        }
    }

    @PostMapping("/password/reset-password-request")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPasswordRequest(request.getEmail());
            return ResponseEntity.ok("Reset password link sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ResetPassword request, BindingResult bindingResult) {
        //TODO After all work is done, add documentation
        //TODO migrate this peace of code to BindingResultService
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            StringBuilder builder = new StringBuilder();
            for (ObjectError error : errors) {
                builder.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(builder.toString());
        }

        try {
            authService.validateResetPasswordToken(request.getToken(), request.getPassword());
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}