package de.ecommerce.security.services;

import de.ecommerce.security.models.PersonalUserToken;
import de.ecommerce.security.models.User;
import de.ecommerce.security.repositories.UserRepository;
import de.ecommerce.security.repositories.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author EgorBusuioc
 * 26.06.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleActionsService {

    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 3 * * *")
    public void checkExpiredTokens() {

        List<PersonalUserToken> expiredTokens = userTokenRepository.findAll();

        for (PersonalUserToken token : expiredTokens) {
            if (token.getExpirationDate().isBefore(java.time.LocalDateTime.now())) {
                userTokenRepository.delete(token);
                log.info("Deleted expired token: {}", token.getToken());
            }
        }

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (!user.isActive()) {
                userRepository.delete(user);
                log.info("Deleted not activated user: {}", user.getEmail());
            }
        }
    }
}
