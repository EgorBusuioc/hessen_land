package de.ecommerce.security.dto;

import de.ecommerce.security.models.enums.Gender;
import de.ecommerce.security.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitizenUserDTO {
    private String userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String identificationNumber;
    private LocalDate birthDate;
    private boolean isActive = false;
    private LocalDateTime creationDate;
    private Role role;
    private Gender gender;
}
