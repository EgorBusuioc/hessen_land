package de.ecommerce.security.models;

import de.ecommerce.security.models.enums.Gender;
import de.ecommerce.security.models.enums.Role;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Transient
    private String firstName;

    @Transient
    private String lastName;

    @Transient
    private String identificationNumber;

    @Transient
    private LocalDate birthDate;

    @Column(name = "is_active")
    private boolean isActive = false;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "token_id")
    private PersonalUserToken token;

    @PrePersist
    public void init(){
        ULID ulid = new ULID();
        this.userId = ulid.nextULID();
        creationDate = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
