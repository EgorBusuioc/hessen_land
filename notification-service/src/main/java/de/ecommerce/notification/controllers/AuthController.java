package de.ecommerce.notification.controllers;

import de.ecommerce.notification.dto.EmailRequest;
import de.ecommerce.notification.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EgorBusuioc
 * 13.06.2025
 */
@RestController
@RequestMapping("/mail/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/activate-account")
    public ResponseEntity<String> activateAccount(@RequestBody EmailRequest activate) {

        authService.sendActivationLink(activate);
        return ResponseEntity.ok("Activation link sent successfully");
    }
}
