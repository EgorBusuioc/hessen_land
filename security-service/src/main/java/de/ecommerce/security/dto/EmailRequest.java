package de.ecommerce.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 14.06.2025
 */
@AllArgsConstructor
@Getter
@Setter
public class EmailRequest {
    private String email;
    private String token;
}
