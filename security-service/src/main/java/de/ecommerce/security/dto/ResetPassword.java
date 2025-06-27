package de.ecommerce.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EgorBusuioc
 * 26.06.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPassword {
    private String token;

    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one digit")
    private String password;
}
