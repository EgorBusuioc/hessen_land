package de.ecommerce.security.repositories;

import de.ecommerce.security.models.PersonalUserToken;
import de.ecommerce.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(PersonalUserToken token);
}
