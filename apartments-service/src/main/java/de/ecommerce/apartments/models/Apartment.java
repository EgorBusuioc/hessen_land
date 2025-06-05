package de.ecommerce.apartments.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author EgorBusuioc
 * 05.06.2025
 */
@Entity
@Data
@Table(name = "apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "apartment_name", unique = true)
    private String apartmentName;

    @Column(name = "apartment_price")
    private Integer price;

    @Column(name = "description")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime creationDate;

}
