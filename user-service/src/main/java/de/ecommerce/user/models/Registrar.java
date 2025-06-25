package de.ecommerce.user.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */
@Entity
@Table(name = "registrars")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Registrar extends AbstractUser{

    @Column(name = "work_number", unique = true, length = 10)
    private String workNumber;
}
