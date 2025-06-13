package de.ecommerce.apartments.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author EgorBusuioc
 * 05.06.2025
 */
@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name= "phone_number")
    private String phoneNumber = null;

    @Column(name = "is_landlord")
    private boolean isLandlord = false;
}
