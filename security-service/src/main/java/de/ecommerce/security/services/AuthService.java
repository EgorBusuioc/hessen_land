package de.ecommerce.security.services;

import de.ecommerce.security.dto.LoginRequest;
import de.ecommerce.security.models.User;
import de.ecommerce.security.models.enums.Role;
import de.ecommerce.security.repositories.UserRepository;
import de.ecommerce.security.token.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Registers a new user in the system.
     * <p>
     * The method first checks if the email is already in use. If not, it sets the user's
     * role to {@code ROLE_USER}, encrypts the password, and persists the user to the database.
     * </p>
     *
     * @param user the user object to be registered
     * @throws IllegalArgumentException\ if a user with the same email already exists
     */
    @Transactional
    public void registerNewUser(User user) throws RuntimeException{

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new IllegalArgumentException("A user with this email already exists.");

        user.setRole(Role.ROLE_USER); // Set the user as USER by default
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encoding the password
        user.init();
        sendUserToKafka(user.getUserId());
        userRepository.save(user); // Saving the user into the database
        log.info("User created: Email: {}", user.getEmail());
    }

    /**
     * Authenticates an existing user using their email and password.
     * <p>
     * The method uses the {@code AuthenticationManager} to authenticate the user. If successful,
     * it generates a JWT token for the user and returns it.
     * </p>
     *
     * @param loginRequest the login request containing email and password
     * @return a JWT token if authentication is successful
     * @throws IllegalArgumentException if authentication fails
     */
    public String findExistingUserByEmail(LoginRequest loginRequest) {
        try {
            User user = (User) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            ).getPrincipal();
            log.info("Authentication successful for user: {}", loginRequest.getEmail());
            log.info("User have been found: Email: {}", loginRequest.getEmail());
            log.info("JWT token generating...");
            return jwtUtils.generateToken(user);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    public void sendUserToKafka(String userId) throws RuntimeException {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("user-topic", userId);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send user to Kafka topic: {}", ex.getMessage());
                throw new RuntimeException("Failed to send user to Kafka topic");
            } else {
                log.info("User sent to Kafka topic successfully: {}", result.getRecordMetadata().offset());
            }
        });
    }
}
