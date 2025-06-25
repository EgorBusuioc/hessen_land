package de.ecommerce.user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
@Entity
@Table(name = "citizen_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CitizenUser extends AbstractUser{

    @Column(name = "identification_number", unique = true, length = 11)
    private String identificationNumber;

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @Column(name = "is_verified")
    private boolean isVerified = false;
}
