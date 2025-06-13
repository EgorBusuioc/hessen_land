package de.ecommerce.notification.dto;

import lombok.Getter;

/**
 * @author EgorBusuioc
 * 13.06.2025
 */
@Getter
public class EmailRequest {
    private String email;
    private String token;
}
