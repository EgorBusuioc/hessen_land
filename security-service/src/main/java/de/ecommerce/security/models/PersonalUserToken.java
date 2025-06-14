package de.ecommerce.security.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author EgorBusuioc
 * 14.06.2025
 */
@Entity
@Table(name = "personal_user_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PersonalUserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "token", unique = true)
    private String token;

    @OneToOne(mappedBy = "token")
    private User user;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expirationDate;

    @PrePersist
    private void setExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusHours(2); // Token valid for 2 hours
    }

    public PersonalUserToken(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
