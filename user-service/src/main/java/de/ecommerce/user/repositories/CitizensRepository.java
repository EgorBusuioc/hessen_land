package de.ecommerce.user.repositories;

import de.ecommerce.user.models.CitizenUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
public interface CitizensRepository extends JpaRepository<CitizenUser, String> {
}
