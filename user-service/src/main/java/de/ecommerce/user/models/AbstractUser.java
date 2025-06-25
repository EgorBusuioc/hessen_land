package de.ecommerce.user.models;

import de.ecommerce.user.models.roles.Gender;
import de.ecommerce.user.models.roles.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EgorBusuioc
 * 25.06.2025
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class AbstractUser {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
