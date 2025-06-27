package de.ecommerce.security.controllers;

import de.ecommerce.security.dto.LoginRequest;
import de.ecommerce.security.dto.RegistrationDTO;
import de.ecommerce.security.dto.ResetPassword;
import de.ecommerce.security.dto.ResetPasswordRequest;
import de.ecommerce.security.services.AuthService;
import de.ecommerce.security.services.BindingResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


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
    private final BindingResultService bindingResultService;

    /**
     * Registers a new user with the provided details.
     *
     * @param user the user details for registration
     * @param bindingResult the result of the validation
     * @return a response entity with the registration status
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationDTO user, BindingResult bindingResult) {

        ResponseEntity<String> errorResponse = bindingResultService.getErrorMessage(bindingResult);
        if (errorResponse.getStatusCode().isError()) {
            return errorResponse;
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
    /**
     * Activates a user account based on the provided activation token.
     * <p>
     * This endpoint validates the activation token and activates the user account if the token is valid.
     * If the token is invalid or expired, it returns a forbidden status with an error message.
     * </p>
     *
     * @param token the activation token provided by the user
     * @return {@link ResponseEntity} containing a success message if activation is successful,
     *         or a forbidden status with an error message if activation fails
     */
    @GetMapping("/activate-account")
    public ResponseEntity<String> activateUser(@RequestParam String token) {
        try {
            authService.validateActivationToken(token);
            return ResponseEntity.ok("User activated successfully, now you can login");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to activate user");
        }
    }

    /**
     * Handles a password reset request for a user.
     * <p>
     * This method accepts the user's email, generates a reset password token,
     * and sends a reset password link to the provided email address.
     * </p>
     *
     * @param request the object containing the user's email
     * @return {@link ResponseEntity} with a success message if the link is sent successfully,
     *         or an error message if the operation fails
     */
    @PostMapping("/password/reset-password-request")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPasswordRequest(request.getEmail());
            return ResponseEntity.ok("Reset password link sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Changes the user's password using the provided reset token and new password.
     * <p>
     * This endpoint validates the reset token and updates the user's password if the token is valid.
     * If the token is invalid or expired, it returns an error message.
     * </p>
     *
     * @param request the object containing the reset token and new password
     * @param bindingResult the result of the validation
     * @return {@link ResponseEntity} with a success message if the password is reset successfully,
     *         or an error message if the operation fails
     */
    @PostMapping("/password/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ResetPassword request, BindingResult bindingResult) {
        ResponseEntity<String> errorResponse = bindingResultService.getErrorMessage(bindingResult);
        if (errorResponse.getStatusCode().isError()) {
            return errorResponse;
        }

        try {
            authService.validateResetPasswordToken(request.getToken(), request.getPassword());
            return ResponseEntity.ok("Password has been successfully reset.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}