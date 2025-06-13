package de.ecommerce.security.models;

import de.ecommerce.security.models.enums.Role;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    private String userId;

    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should have a valid format - \"mail@gmail.com\"")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password should not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter and one digit"
    )
    @Column(name = "password")
    private String password;

    @NotBlank(message = "First name should not be empty")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    Role role;

    public void init(){
        ULID ulid = new ULID();
        this.userId = ulid.nextULID();
        creationDate = LocalDateTime.now();
    }

    public String getFormattedCreationDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMMM/yyyy", new Locale("en"));
        return creationDate.format(formatter);
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
        return true;
    }
}
