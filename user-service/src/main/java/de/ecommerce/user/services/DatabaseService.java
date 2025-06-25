package de.ecommerce.user.services;

import de.ecommerce.user.models.CitizenUser;
import de.ecommerce.user.repositories.CitizensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final CitizensRepository citizensRepository;

    public void saveCitizenUser(CitizenUser citizenUser) {
        citizensRepository.save(citizenUser);
        log.info("Citizen user has been saved");
    }
}
