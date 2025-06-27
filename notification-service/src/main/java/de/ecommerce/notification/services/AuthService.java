package de.ecommerce.notification.services;

import de.ecommerce.notification.dto.EmailRequest;
import de.ecommerce.notification.dto.enums.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "email-events", groupId = "notification-service")
    public void sendActivationLink(EmailRequest activate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(activate.getEmail());

        if (activate.getRequestType().equals(RequestType.ALREADY_ACTIVATED_USER))
            mailSender.send(alreadyActivatedUserMailSender(message));

        if (activate.getRequestType().equals(RequestType.NOT_EXISTING_USER))
            mailSender.send(notExistingUserMailSender(message, activate));

        if (activate.getRequestType().equals(RequestType.RESET_PASSWORD))
            mailSender.send(resetPasswordLinkSender(message, activate));

        log.info("Activation link sent to: {}", activate.getEmail());
    }

    private SimpleMailMessage alreadyActivatedUserMailSender(SimpleMailMessage email) {
        email.setSubject("Thank you for activating your account");
        email.setText("""
                Thank you for activating your account, now you can log in to your account.
                If you have any questions, please contact our support team.
                
                Best regards,
                HessenLand Team""");
        return email;
    }

    private SimpleMailMessage notExistingUserMailSender(SimpleMailMessage email, EmailRequest activate) {
        email.setSubject("Activate your account");
        email.setText("\nHi, you've just created account in our service, please go to link below and activate your account:\n" +
                "http://localhost:8081/auth/activate-account?token=" + activate.getToken() +
                "\nActivation link will be valid for 2 hours.\n\n" +
                "\n\nIf you didn't create an account, please ignore this email.\n");
        return email;
    }

    private SimpleMailMessage resetPasswordLinkSender(SimpleMailMessage email, EmailRequest activate) {
        email.setSubject("Reset your password");
        email.setText("\nHi, you requested to reset your password, please go to link below and reset your password:\n" +
                "http://localhost:8081/auth/password/reset?token=" + activate.getToken() +
                "\nReset link will be valid for 2 hours.\n\n" +
                "\n\nIf you didn't request to reset your password, please ignore this email.\n");
        return email;
    };
}
