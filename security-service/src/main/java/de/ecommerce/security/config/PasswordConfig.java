package de.ecommerce.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class is responsible for configuring the password encoder used in the application.
 * @author EgorBusuioc
 * 27.05.2025
 */
@Configuration
public class PasswordConfig {

    /**
     * This method creates a bean of PasswordEncoder using BCryptPasswordEncoder with a strength of 8.
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
