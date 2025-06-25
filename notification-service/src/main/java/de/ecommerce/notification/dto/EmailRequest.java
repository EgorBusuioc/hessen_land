package de.ecommerce.notification.dto;

import de.ecommerce.notification.dto.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 13.06.2025
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequest {
    private String email;
    private String token;
    private RequestType requestType;
}
