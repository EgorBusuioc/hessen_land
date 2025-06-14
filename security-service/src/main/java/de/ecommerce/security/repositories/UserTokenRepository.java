package de.ecommerce.security.repositories;

import de.ecommerce.security.models.PersonalUserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author EgorBusuioc
 * 14.06.2025
 */
public interface UserTokenRepository extends JpaRepository<PersonalUserToken, Long> {
    Optional<PersonalUserToken> findByToken(String token);
}
