package de.ecommerce.security.dto;

import de.ecommerce.security.models.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author EgorBusuioc
 * 26.06.2025
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDTO {
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should have a valid format - \"mail@gmail.com\"")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one digit")
    private String password;

    @NotBlank(message = "First name should not be empty")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    private String lastName;

    private String identificationNumber;

    private LocalDate birthDate;

    private Gender gender;
}
