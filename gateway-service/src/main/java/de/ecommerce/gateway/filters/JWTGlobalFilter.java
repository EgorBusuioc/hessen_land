package de.ecommerce.gateway.filters;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * This class is responsible for decoding JWT tokens and adding user information to the request headers.
 * It implements the GlobalFilter interface and is used in the Spring Cloud Gateway.
 * @author EgorBusuioc
 * 27.05.2025
 */
@Component
@RequiredArgsConstructor
public class JWTGlobalFilter implements GlobalFilter, Ordered {

    private final ReactiveJwtDecoder jwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwtToken = authHeader.substring(7);

        return jwtDecoder.decode(jwtToken).flatMap(token -> {
            String userEmail = token.getClaimAsString("username");
            String userRole = token.getClaimAsString("role");

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Email", userEmail)
                    .header("X-User-Role", userRole)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
