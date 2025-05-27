package de.ecommerce.gateway.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * This class is responsible for handling exceptions related to authentication and authorization.
 * @author EgorBusuioc
 * 27.05.2025
 */
@Slf4j
public class CustomExceptionHandlers {

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String message = "Unauthorized: please provide a valid token";

            if (ex.getCause() != null && ex.getCause().getMessage() != null &&
                    ex.getCause().getMessage().toLowerCase().contains("expired")) {
                message = "Token expired. Please log in again.";
            }

            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            byte[] bytes = ("{\"message\":\"" + message + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] bytes = "{\"message\":\"Access denied: insufficient permissions\"}"
                    .getBytes(StandardCharsets.UTF_8);
            log.info("Access denied: insufficient permissions for user");
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        };
    }
}
