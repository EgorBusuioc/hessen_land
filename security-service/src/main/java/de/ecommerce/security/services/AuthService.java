package de.ecommerce.security.services;

import de.ecommerce.security.dto.CitizenUserDTO;
import de.ecommerce.security.dto.EmailRequest;
import de.ecommerce.security.dto.LoginRequest;
import de.ecommerce.security.dto.RegistrationDTO;
import de.ecommerce.security.dto.enums.RequestType;
import de.ecommerce.security.models.PersonalUserToken;
import de.ecommerce.security.models.User;
import de.ecommerce.security.models.enums.Role;
import de.ecommerce.security.repositories.UserRepository;
import de.ecommerce.security.repositories.UserTokenRepository;
import de.ecommerce.security.token.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
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
    private final UserTokenRepository userTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    /**
     * Registers a new user in the system.
     * <p>
     * The method first checks if the email is already in use. If not, it sets the user's
     * role to {@code ROLE_USER}, encrypts the password, and persists the user to the database.
     * </p>
     *
     * @param userDTO the user object to be registered
     * @throws IllegalArgumentException\ if a user with the same email already exists
     */
    @Transactional
    public void registerNewUser(RegistrationDTO userDTO) throws IllegalArgumentException{
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
            throw new IllegalArgumentException("A user with this email already exists.");

        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encoding the password
        userRepository.save(user); // Saving the user into the database
        log.info("User created: Email: {}", user.getEmail());
        sendActivationLink(user);
        user.setRole(Role.CITIZEN);
        sendUserToKafka(modelMapper.map(user, CitizenUserDTO.class));
        log.info("User's activation link has been sent to: {}", user.getEmail());
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
    public String findExistingUserByEmail(LoginRequest loginRequest){
        try {
            User user = (User) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            ).getPrincipal();

            log.info("Authentication successful for user: {}", loginRequest.getEmail());
            log.info("User have been found: Email: {}", loginRequest.getEmail());
            log.info("JWT token generating...");
            return jwtUtils.generateToken(user);
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    /**
     * Handles a password reset request for a user.
     * <p>
     * This method checks if the user exists based on the provided email. If the user already has an activation
     * or reset token, an exception is thrown. Otherwise, a new reset password token is generated and a reset
     * password link is sent to the user's email.
     * </p>
     *
     * @param email the email of the user requesting a password reset
     * @throws UsernameNotFoundException if the user with the provided email does not exist
     * @throws IllegalStateException if the user already has an activation or reset token
     */
    @Transactional
    public void resetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with this email does not exist"));

        if (user.getToken() != null) {
            log.error("User with email {} already has an activation or reset token", email);
            throw new IllegalStateException("User already has an activation token.");
        }
        String resetPasswordToken = UUID.randomUUID().toString();
        sentResetPasswordLink(user, resetPasswordToken);
    }

    /**
     * Sends a reset password link to the user.
     * <p>
     * This method creates a reset password token, saves it in the database, and sends an event to Kafka
     * to deliver the reset password link to the user's email.
     * </p>
     *
     * @param user the user object to whom the reset password link will be sent
     * @param resetPasswordToken the token for resetting the password
     */
    @Transactional
    protected void sentResetPasswordLink(User user, String resetPasswordToken) {
        PersonalUserToken personalUserToken = new PersonalUserToken(resetPasswordToken, user);
        user.setToken(personalUserToken);
        userRepository.save(user);

        log.info("Reset password token generated and saved for user: {}", user.getEmail());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("email-events", new EmailRequest(user.getEmail(), personalUserToken.getToken(), RequestType.RESET_PASSWORD));
        tryToSendMessageToKafka(future);
        log.info("Reset password link sent to user: {}", user.getEmail());
    }

    /**
     * Validates the reset password token and updates the user's password.
     * <p>
     * This method checks if the provided token exists and is not expired. If valid, it updates the user's password
     * and removes the token from the user. If the token is invalid or expired, an exception is thrown.
     * </p>
     *
     * @param token the reset password token to be validated
     * @param password the new password to be set for the user
     * @throws UsernameNotFoundException if the token is not found or the user does not exist
     * @throws Exception if the token is expired
     */
    @Transactional
    public void validateResetPasswordToken(String token, String password) throws Exception {
        final PersonalUserToken passwordToken = userTokenRepository.findByToken(token).orElse(null);

        if (!isTokenFound(passwordToken))
            throw new UsernameNotFoundException("Token not found");

        if (isTokenExpired(passwordToken)) {
            log.error("Token expired for user: {}", passwordToken.getUser().getEmail());
            throw new Exception("Token expired");
        }

        User user = userRepository.findByToken(passwordToken)
                .orElseThrow(() -> new UsernameNotFoundException("User with this token does not exist"));

        user.setPassword(passwordEncoder.encode(password)); // Encoding the new password
        user.setToken(null);
        userRepository.save(user);

        log.info("User - {} changed his password.", user.getEmail());
    }

    @Transactional
    protected void sendActivationLink(User user) {
        String token = UUID.randomUUID().toString();
        PersonalUserToken personalUserToken = new PersonalUserToken(token, user);
        user.setToken(personalUserToken);
        userRepository.save(user);

        log.info("Token generated and saved for user: {}", user.getEmail());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("email-events",  new EmailRequest(user.getEmail(), personalUserToken.getToken(), RequestType.NOT_EXISTING_USER));
        tryToSendMessageToKafka(future);
        log.info("Activation link sent to user: {}", user.getEmail());
    }

    /**
     * Sends an activation link to the user.
     * <p>
     * This method generates a unique activation token, saves it in the database, and sends an event to Kafka
     * to deliver the activation link to the user's email.
     * </p>
     *
     * @param user the user object to whom the activation link will be sent
     */
    private void sendUserToKafka(CitizenUserDTO user) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("user-sending-events", user);
        tryToSendMessageToKafka(future);
        log.info("User has been sent to Kafka topic: {}", user.getEmail());
    }

    /**
     * Sends a thank-you email to the user.
     * <p>
     * This method sends an event to Kafka to deliver a thank-you email to the user's email address
     * after the user has been successfully activated.
     * </p>
     *
     * @param user the user object to whom the thank-you email will be sent
     */
    protected void sendThanksEmail(User user) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("email-events",  new EmailRequest(user.getEmail(), null, RequestType.ALREADY_ACTIVATED_USER));
        tryToSendMessageToKafka(future);
        log.info("Thanking Email was sent to User: {}", user.getEmail());
    }

    /**
     * Sends a thank-you email to the user.
     * <p>
     * This method sends an event to Kafka to deliver a thank-you email to the user's email address
     * after the user has been successfully activated.
     * </p>
     *
     * @param future the user object to whom the thank-you email will be sent
     */
    private void tryToSendMessageToKafka(CompletableFuture<SendResult<String, Object>> future) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send user to Kafka topic: {}", ex.getMessage());
                throw new RuntimeException("Failed to send user to Kafka topic");
            } else {
                log.info("User sent to Kafka topic successfully: {}", result.getRecordMetadata().offset());
            }
        });
    }

    /**
     * Validates the activation token and activates the user.
     * <p>
     * This method checks if the provided token exists and is not expired. If valid, it activates the user,
     * removes the token, and sends a thank-you email. If the token is invalid or expired, an exception is thrown.
     * </p>
     *
     * @param token the activation token to be validated
     * @throws UsernameNotFoundException if the token is not found
     * @throws Exception if the token is expired
     */
    @Transactional
    public void validateActivationToken(String token) throws Exception {
        final PersonalUserToken passwordToken = userTokenRepository.findByToken(token).orElse(null);

        if (!isTokenFound(passwordToken))
            throw new UsernameNotFoundException("Token not found");

        if (isTokenExpired(passwordToken)) {
            log.error("Token expired for user: {}", passwordToken.getUser().getEmail());
            throw new Exception("Token expired");
        }

        User user = passwordToken.getUser();
        user.setActive(true); // Activate the user
        user.setToken(null);
        userRepository.save(user);
        log.info("User has been activated: {}", user.getEmail());
        sendThanksEmail(user);

        log.info("Token has been removed from user: {}", passwordToken.getUser().getEmail());
    }

    private boolean isTokenFound(PersonalUserToken passwordToken) {
        return passwordToken != null && passwordToken.getUser() != null;
    }

    private boolean isTokenExpired(PersonalUserToken passwordToken) {
        return passwordToken.getExpirationDate().isBefore(LocalDateTime.now());
    }
}
