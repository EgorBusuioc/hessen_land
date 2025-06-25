package de.ecommerce.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import de.ecommerce.security.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for generating JWT tokens
 * @author EgorBusuioc
 * 27.05.2025
 */
@Component
@Slf4j
public class JWTUtils {

    /**
     * Takes the private key from the resources folder and loads it
     * @throws Exception if the key cannot be loaded
     */
    private static RSAPrivateKey loadPrivateKey() throws Exception {

        ClassPathResource resource = new ClassPathResource("private.pem");
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    /**
     * Generates a JWT token for the given user
     * @param user the user to generate the token for
     * @return the generated token
     */
    public String generateToken(User user) {

        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(30).toInstant());
        RSAPrivateKey privateKey;
        try {
            privateKey = loadPrivateKey();
        } catch (Exception e){
            log.error("Error loading private key", e);
            return null;
        }

        return JWT.create()
                .withSubject("User details")
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().name())
                .withClaim("userId", user.getUserId())
                .withIssuedAt(new Date())
                .withIssuer("BUSUIOC-SECURITY")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.RSA256(null, privateKey));
    }
}

