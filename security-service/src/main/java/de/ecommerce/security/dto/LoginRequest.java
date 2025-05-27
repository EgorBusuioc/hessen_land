package de.ecommerce.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should have a valid format - \"mail@gmail.com\"")
    private String email;

    @NotBlank(message = "Password should not be empty")
    private String password;
}
