package de.ecommerce.notification.services;

import de.ecommerce.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void sendActivationLink(EmailRequest activate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ietecproject@gmail.com");
        message.setTo(activate.getEmail());
        message.setSubject("Reset your password");

        message.setText("\nHi, you've just created account in our service, please go to link below and activate your account:\n" +
                "http://localhost:8081/auth/password/activate-account?token=" + activate.getToken());

        mailSender.send(message);
        log.info("Reset password email sent to: {}", activate.getEmail());
    }
}
