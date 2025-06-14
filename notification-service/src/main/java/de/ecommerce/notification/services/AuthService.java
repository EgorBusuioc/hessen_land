package de.ecommerce.notification.services;

import de.ecommerce.notification.dto.EmailRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author EgorBusuioc
 * 13.06.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender mailSender;

    private final String fromEmail = System.getenv("MAIL_USERNAME");

    public void sendActivationLink(EmailRequest activate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(activate.getEmail());
        message.setSubject("Activate your account");


        message.setText("\nHi, you've just created account in our service, please go to link below and activate your account:\n" +
                "http://localhost:8081/auth/password/activate-account?token=" + activate.getToken() +
                "\nActivation link will be valid for 2 hours.\n\n" +
                "\n\nIf you didn't create an account, please ignore this email.\n");

        mailSender.send(message);
        log.info("Activation link sent to: {}", activate.getEmail());
    }
}
