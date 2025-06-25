package de.ecommerce.user.services;

import de.ecommerce.user.models.CitizenUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaListenerService {

    private final DatabaseService databaseService;

    @KafkaListener(topics = "user-sending-events", groupId = "user-service")
    public void consumeNewUser(CitizenUser citizenUser) {
        log.info("Received a new citizen user event");
        databaseService.saveCitizenUser(citizenUser);
    }
}
